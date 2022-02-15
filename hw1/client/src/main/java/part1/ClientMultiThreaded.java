package part1;

import io.swagger.client.ApiClient;
import io.swagger.client.api.SkiersApi;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientMultiThreaded {
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

        int numThreadsPhase1 = numThreads / 4;
        int numThreadsPhase2 = numThreads;
        int numThreadsPhase3 =(int)Math.ceil(numThreads * 0.1);

        AtomicInteger numSuccessReq = new AtomicInteger(0);
        AtomicInteger numUnsuccessReq = new AtomicInteger(0);
        CountDownLatch totalCompleted = new CountDownLatch(numThreadsPhase1 + numThreadsPhase2 + numThreadsPhase3);

        int numPostsPhase1 = (int)Math.ceil((numRuns * 0.2) * (numSkiers / numThreadsPhase1));
        int numPostsPhase2 = (int)Math.ceil((numRuns * 0.6) * (numSkiers / numThreadsPhase2));
        int numPostsPhase3 = (int)Math.ceil(numRuns * 0.1 * numThreadsPhase3);

        // trigger phase 2 when 20% of phase 1 completed;
        int numTriggerPhase2 = (int)Math.ceil(numThreadsPhase1 * PHASE_PCT);
        CountDownLatch completedPhase2 = new CountDownLatch(numTriggerPhase2);
        // trigger phase 3 when 20% of phase 2 completed;
        int numTriggerPhase3 = (int)Math.ceil(numThreadsPhase2 * PHASE_PCT);
        CountDownLatch completedPhase3 = new CountDownLatch(numTriggerPhase3);

        int startTimePhase1 = 1;
        int endTimePhase1 = 90;
        int startTimePhase2 = 91;
        int endTimePhase2 = 360;
        int startTimePhase3 = 361;
        int endTimePhase3 = 420;

        Phase phase1 = new Phase(numThreadsPhase1, numSkiers, startTimePhase1, endTimePhase1,
                numLifts, numPostsPhase1, numSuccessReq, numUnsuccessReq, totalCompleted,
                completedPhase2, skiersApi);
        Phase phase2 = new Phase(numThreadsPhase2, numSkiers, startTimePhase2, endTimePhase2,
                numLifts, numPostsPhase2, numSuccessReq, numUnsuccessReq, totalCompleted,
                completedPhase3, skiersApi);
        Phase phase3 = new Phase(numThreadsPhase3, numSkiers, startTimePhase3, endTimePhase3,
                numLifts, numPostsPhase3, numSuccessReq, numUnsuccessReq, totalCompleted,
                completedPhase3, skiersApi);


        // run phase
        long start = System.currentTimeMillis();

        System.out.println("------  PHASE 1  ------");
        phase1.start();
        completedPhase2.await();

        System.out.println("------  PHASE 2  ------");
        phase2.start();
        completedPhase3.await();

        System.out.println("------  PHASE 3  ------");
        phase3.start();
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
