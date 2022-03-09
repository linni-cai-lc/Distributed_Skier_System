import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

public class MultiThreadedConsumer {
    private static String RESORTS = "resorts";
    private static String SKIERS = "skiers";
    private static String SERVER_QUEUE = "server_queue";
    private static int PER_CONSUMER_LIMIT = 1;
    // skierId -> list of LiftRides
    private static ConcurrentMap<Integer, List<LiftRide>> skierLiftRideMap = new ConcurrentHashMap<>();
    // resortId -> list of seasons
    private static ConcurrentMap<Integer, List<String>> resortSeasonMap = new ConcurrentHashMap<>();

    public static void main(String args[]) throws IOException, TimeoutException {
        Config config = ConfigProcessor.processConfig();
        if (config == null) {
            throw new IllegalArgumentException("Consumer config property is invalid.");
        }
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(config.getHost());
        connectionFactory.setUsername(config.getUsername());
        connectionFactory.setPassword(config.getPassword());
        Connection connection = connectionFactory.newConnection();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Channel channel = connection.createChannel();
                    channel.queueDeclare(SERVER_QUEUE, true, false, false, null);
                    channel.basicQos(PER_CONSUMER_LIMIT);
                    System.out.println("[*] Waiting for messages. To exit press CTRL+C");
                    DeliverCallback deliverCallback = (tag, delivery) -> {
                        String msg = new String(delivery.getBody(), "UTF-8");
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        System.out.println(String.format("[x] Received '%s'", msg));
                        updateInfoInMap(msg);
                    };
                    channel.basicConsume(SERVER_QUEUE, false, deliverCallback, tag -> {});
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < config.getMaxThreads(); i++) {
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    private synchronized static void updateInfoInMap(String msg) throws InvalidPropertiesFormatException {
        Gson gson = new Gson();
        Message message = gson.fromJson(msg, Message.class);
        Integer time = message.getTime();
        Integer liftId = message.getLiftID();
        Integer waitTime = message.getWaitTime();
        Integer resortId = message.getResortId();
        Integer skierId = message.getSkierID();
        String season = message.getSeason();
        String type = message.getType();

        if (type.equals(RESORTS)) {
            List<String> seasonList;
            if (!resortSeasonMap.containsKey(resortId)) {
                seasonList = new ArrayList<>();
            } else {
                seasonList = resortSeasonMap.get(resortId);
            }
            seasonList.add(season);
            resortSeasonMap.put(resortId, seasonList);
            System.out.println("new resortSeasonMap size: " + resortSeasonMap.size());
            System.out.println("current seasonList size: " + seasonList.size());
        } else if (type.equals(SKIERS)) {
            LiftRide liftRide = new LiftRide(liftId, time, waitTime);
            List<LiftRide> liftRideList;
            if (!skierLiftRideMap.containsKey(skierId)) {
                liftRideList = new ArrayList<>();
            } else {
                liftRideList = skierLiftRideMap.get(skierId);
            }
            liftRideList.add(liftRide);
            skierLiftRideMap.put(skierId, liftRideList);
            System.out.println("new skierLiftRideMap size: " + skierLiftRideMap.size());
            System.out.println("current liftRideList size: " + liftRideList.size());
        } else {
            throw new InvalidPropertiesFormatException("The post message format is not valid.");
        }
    }
}
