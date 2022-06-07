import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.concurrent.TimeoutException;

public class MultiThreadedConsumer {
    private static String USERNAME = "test";
    private static String PASSWORD = "test";
    private static String VHOST = "/";
    private static String QUEUE_NAME = "consumer_skier_2_queue";

    private static int MAX_THREAD = 256;
    private static final String EXCHANGE_NAME = "logs";
    private static final String EXCHANGE_TYPE = "fanout";
    private static SkierDao skierDao;

    public static void main(String args[]) throws IOException, TimeoutException {
        JedisPool pool = new JedisPool(IPAddress.TOTAL_VERTICAL_LIST_IP, 6379);
        try (Jedis jedis = pool.getResource()) {
//            jedis.flushDB();
            skierDao = new SkierDao(jedis);
        }

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(IPAddress.RMQ_IP);
        connectionFactory.setUsername(USERNAME);
        connectionFactory.setPassword(PASSWORD);
        connectionFactory.setVirtualHost(VHOST);
        Connection connection = connectionFactory.newConnection();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Channel channel = connection.createChannel();
                    channel.queueDeclare(QUEUE_NAME, true, false, false, null);
                    channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
                    channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
                    System.out.println("[*] Waiting for messages. To exit press CTRL+C");
                    DeliverCallback deliverCallback = (tag, delivery) -> {
                        String message = new String(delivery.getBody(), "UTF-8");
                        // System.out.println(" [x] Received '" + message + "'");
                        createSkier(message);
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    };
                    channel.basicConsume(QUEUE_NAME, false, deliverCallback, tag -> {});
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

    private synchronized static void createSkier(String msg) throws InvalidPropertiesFormatException {
        Gson gson = new Gson();
        LiftRide newLiftRide = gson.fromJson(msg, LiftRide.class);
        skierDao.createSkier(newLiftRide);
    }
}
