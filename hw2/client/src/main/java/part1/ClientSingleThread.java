package part1;

import io.swagger.client.ApiClient;
import io.swagger.client.api.SkiersApi;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientSingleThread {
    //    private static final String PATH = "/hw1_war_exploded/ski"; // LOCAL
    private static final String PATH = "/hw1_war/ski"; // AWS
    private static final String HTTP_PREFIX = "http://";
    private static final double PHASE_PCT = 0.2;

    public static void main(String[] args) throws InterruptedException {
        // LOCAL --num_threads 64 --num_skiers 128 --num_lifts 40 --num_runs 20 --ip_address 152.44.141.6:8080
        // AWS   --num_threads 64 --num_skiers 128 --num_lifts 40 --num_runs 20 --ip_address 54.200.234.195:8080
        CommandLineParser parser = CommandLineParser.parseCommandArgs(args);
        int numThreads = parser.numThreads;
        int numSkiers = parser.numSkiers;
        int numLifts = parser.numLifts;
        int numRuns = parser.numRuns;
        String ipAddress = parser.ipAddress;

        // LOCAL http://152.44.141.6:8080/hw1_war_exploded/ski
        // AWS   http://54.200.234.195:8080/hw1_war/ski
        String path = HTTP_PREFIX + ipAddress + PATH;
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(path);
        SkiersApi skiersApi = new SkiersApi(apiClient);

        int numThreadsPhase1 = numThreads;

        AtomicInteger numSuccessReq = new AtomicInteger(0);
        AtomicInteger numUnsuccessReq = new AtomicInteger(0);
        CountDownLatch totalCompleted = new CountDownLatch(numThreadsPhase1);

        int numPostsPhase1 = 10000;

        int startTimePhase1 = 1;
        int endTimePhase1 = 90;
        int startTimePhase2 = 91;
        int endTimePhase2 = 360;
        int startTimePhase3 = 361;
        int endTimePhase3 = 420;

        Phase phase1 = new Phase(numThreadsPhase1, numSkiers, startTimePhase1, endTimePhase1,
                numLifts, numPostsPhase1, numSuccessReq, numUnsuccessReq, totalCompleted,
                null, skiersApi);

        // run
        long start = System.currentTimeMillis();

        System.out.println("------  PHASE 1  ------");
        phase1.start();
        totalCompleted.await();

        long end = System.currentTimeMillis();

        long duration = end - start;
        long throughput = (numSuccessReq.get() + numUnsuccessReq.get()) / (duration / 1000);
        System.out.println("------  Statistics  ------");
        System.out.println("number of successful requests sent: " + numSuccessReq);
        System.out.println("number of unsuccessful requests: " + numUnsuccessReq);
        System.out.println("the total run time for all phases to complete: " + duration);
        System.out.println("the total throughput in requests per second: " + throughput);
    }

}
