import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import redis.clients.jedis.JedisPooled;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.concurrent.TimeoutException;

public class MultiThreadedConsumer {
    private static String REDIS_HOST = "34.220.110.235";
    private static String RMQ_HOST = "34.219.86.177";
    private static String USERNAME = "test";
    private static String PASSWORD = "test";
    private static String VHOST = "/";
    private static String QUEUE_NAME = "server_queue";

    private static int MAX_THREAD = 256;
    private static String LIFT_RIDE = "lift_ride";
    private static final String EXCHANGE_NAME = "logs";
    private static final String EXCHANGE_TYPE = "fanout";
    private static ResortDao resortDao;

    public static void main(String args[]) throws IOException, TimeoutException {
        JedisPooled jedis = new JedisPooled(REDIS_HOST, 6379);
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(RMQ_HOST);
        connectionFactory.setUsername(USERNAME);
        connectionFactory.setPassword(PASSWORD);
        connectionFactory.setVirtualHost(VHOST);
        Connection connection = connectionFactory.newConnection();
        resortDao = new ResortDao(jedis);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Channel channel = connection.createChannel();
                    channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
                    channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
                    System.out.println("[*] Waiting for messages. To exit press CTRL+C");
                    DeliverCallback deliverCallback = (tag, delivery) -> {
                        String message = new String(delivery.getBody(), "UTF-8");
                        // System.out.println(" [x] Received '" + message + "'");
                        createResort(message);
                    };
                    channel.basicConsume(QUEUE_NAME, true, deliverCallback, tag -> {});
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
    private synchronized static void createResort(String msg) throws InvalidPropertiesFormatException {
        Gson gson = new Gson();
        Message message = gson.fromJson(msg, Message.class);
        Integer liftTime = message.getTime();
        Integer liftId = message.getLiftID();
        String resortId = message.getResortId();
        String skierId = message.getSkierID();
        String seasonId = message.getSeasonID();
        String type = message.getType();
        String dayId = message.getDayID();

        if (type.equals(LIFT_RIDE)) {
            LiftRide liftRide = new LiftRide(liftId, liftTime, seasonId, dayId, skierId, resortId);
            resortDao.createResort(liftRide);
        } else {
            throw new InvalidPropertiesFormatException("The post message format is not valid.");
        }
    }
}
