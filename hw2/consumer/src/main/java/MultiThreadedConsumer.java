import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

public class MultiThreadedConsumer {
    private static String SERVER_QUEUE = "server_queue";
    private static int PER_CONSUMER_LIMIT = 10;
    private ConcurrentMap<Integer, List<SkierInfo>> skierInfoMap = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, List<ResortInfo>> resortInfoMap = new ConcurrentHashMap<>();
    private ConcurrentMap<Integer, List<StatisticsInfo>> statInfoMap = new ConcurrentHashMap<>();

    public static void main(String args[]) throws IOException, TimeoutException {
        ConsumerArgs consumerArgs = ArgsProcessor.processArgs();
        if (consumerArgs == null) {
            throw new IllegalArgumentException("Consumer arg is invalid.");
        }
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(consumerArgs.getHostName());
        connectionFactory.setUsername(consumerArgs.getUserName());
        connectionFactory.setPassword(connectionFactory.getPassword());
        Connection connection = connectionFactory.newConnection();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Channel channel = connection.createChannel();
                    channel.queueDeclare(SERVER_QUEUE, true, false, false, null);
                    channel.basicQos(PER_CONSUMER_LIMIT);
                    System.out.println("*** wait for message, exit with COMMAND+C ***");
                    DeliverCallback deliverCallback = (tag, delivery) -> {
                        String msg = new String(delivery.getBody(), "UTF-8");
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        System.out.println(String.format("*** received message: %s ***", msg));
//                        updateInfoInMap(msg);
                    };
                    channel.basicConsume(SERVER_QUEUE, false, deliverCallback, tag -> {});
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < consumerArgs.getMaxThreads(); i++) {
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    private synchronized static void updateInfoInMap(String msg) {
        Gson gson = new Gson();

    }


}
