import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPooled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

public class MultiThreadedConsumer {
    private static String REDIS_HOST = "18.237.169.196";
    private static String RMQ_HOST = "35.88.248.219";
    private static String USERNAME = "test";
    private static String PASSWORD = "test";
    private static String VHOST = "/";

    private static int MAX_THREAD = 128;
    private static String RESORTS = "resorts";
    private static String SKIERS = "skiers";
    private static String SERVER_QUEUE = "server_queue";
    private static int PER_CONSUMER_LIMIT = 1;
    private static LiftRideDao liftRideDao = new LiftRideDao();

//    // skierId -> list of LiftRides
//    private static ConcurrentMap<Integer, List<LiftRide>> skierLiftRideMap = new ConcurrentHashMap<>();
//    // resortId -> list of seasons
//    private static ConcurrentMap<Integer, List<String>> resortSeasonMap = new ConcurrentHashMap<>();

    public static void main(String args[]) throws IOException, TimeoutException {
        JedisPool pool = new JedisPool(REDIS_HOST, 6379);
        JedisPooled jedis = new JedisPooled(REDIS_HOST, 6379);
        jedis.sadd("planets", "Venus");

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(RMQ_HOST);
        connectionFactory.setUsername(USERNAME);
        connectionFactory.setPassword(PASSWORD);
        connectionFactory.setVirtualHost(VHOST);
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
                        updateInfoInMap(msg);
                    };
                    channel.basicConsume(SERVER_QUEUE, false, deliverCallback, tag -> {});
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < MAX_THREAD; i++) {
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
        Integer seasonId = message.getSeasonID();
        String season = message.getSeason();
        String type = message.getType();
        Integer dayId = message.getDayID();

//        if (type.equals(RESORTS)) {
//            List<String> seasonList;
//            if (!resortSeasonMap.containsKey(resortId)) {
//                seasonList = new ArrayList<>();
//            } else {
//                seasonList = resortSeasonMap.get(resortId);
//            }
//            seasonList.add(season);
//            resortSeasonMap.put(resortId, seasonList);
//        } else if (type.equals(SKIERS)) {
//            LiftRide liftRide = new LiftRide(liftId, time, waitTime, seasonId, dayId, skierId);
//            List<LiftRide> liftRideList;
//            if (!skierLiftRideMap.containsKey(skierId)) {
//                liftRideList = new ArrayList<>();
//            } else {
//                liftRideList = skierLiftRideMap.get(skierId);
//            }
//            liftRideList.add(liftRide);
//            skierLiftRideMap.put(skierId, liftRideList);
//        } else {
//            throw new InvalidPropertiesFormatException("The post message format is not valid.");
//        }
    }
}
