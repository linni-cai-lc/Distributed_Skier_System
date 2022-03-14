package part2;

import io.swagger.client.ApiClient;
import io.swagger.client.api.SkiersApi;
import part1.CommandLineParser;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientMultiThreadedSinglePhase {
    //    private static final String PATH = "/server_war_exploded/ski"; // LOCAL
    private static final String PATH = "/server_war/ski"; // AWS
    private static final String HTTP_PREFIX = "http://";
    private static final double PHASE_PCT = 0.2;

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        // LOCAL --num_threads 64 --num_skiers 128 --num_lifts 40 --num_runs 20 --ip_address 152.44.141.6:8080
        // AWS   --num_threads 64 --num_skiers 128 --num_lifts 40 --num_runs 20 --ip_address 54.200.234.195:8080
        CommandLineParser parser = CommandLineParser.parseCommandArgs(args);
        int numThreads = parser.numThreads;
        int numSkiers = parser.numSkiers;
        int numLifts = parser.numLifts;
        int numRuns = parser.numRuns;
        String ipAddress = parser.ipAddress;

        // LOCAL http://152.44.141.6:8080/server_war_exploded/ski
        // AWS   http://54.200.234.195:8080/server_war/ski
        String path = HTTP_PREFIX + ipAddress + PATH;
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(path);
        SkiersApi skiersApi = new SkiersApi(apiClient);

        int numThreadsPhase2 = numThreads;

        AtomicInteger numSuccessReq = new AtomicInteger(0);
        AtomicInteger numUnsuccessReq = new AtomicInteger(0);
        CountDownLatch totalCompleted = new CountDownLatch(numThreadsPhase2);

        int numPostsPhase2 = (int)Math.ceil((numRuns * 0.6) * (numSkiers / numThreadsPhase2));

        int startTimePhase1 = 1;
        int endTimePhase1 = 90;
        int startTimePhase2 = 91;
        int endTimePhase2 = 360;
        int startTimePhase3 = 361;
        int endTimePhase3 = 420;

        Phase phase2 = new Phase(numThreadsPhase2, numSkiers, startTimePhase2, endTimePhase2,
                numLifts, numPostsPhase2, numSuccessReq, numUnsuccessReq, totalCompleted,
                null, skiersApi);

        // run phase
        long start = System.currentTimeMillis();

        System.out.println("------  PHASE 2  ------");
        phase2.start();
        totalCompleted.await();

        long end = System.currentTimeMillis();

        long duration = end - start;
        long throughput = (numSuccessReq.get() + numUnsuccessReq.get()) / (duration / 1000);
        System.out.println("\n------  Statistics  ------\n");
        System.out.println("------  PART 1  ------");
        System.out.println("number of successful requests sent: " + numSuccessReq);
        System.out.println("number of unsuccessful requests: " + numUnsuccessReq);
        System.out.println("the total run time for all phases to complete: " + duration);
        System.out.println("the total throughput in requests per second: " + throughput);
        System.out.println();

        List<String[]> records = new ArrayList<>();
        records.addAll(phase2.records);
        CSVProcessor processor = new CSVProcessor(records, start);
        processor.generateCSV();

        System.out.println("------  PART 2  ------");
        Calculator calculator = new Calculator(records, start, end);
        System.out.println("mean response time (millisecs): " + calculator.getMeanResponse());
        System.out.println("median response time (millisecs): " + calculator.getMedianResponse());
        System.out.println("throughput: " + calculator.getThroughput());
        System.out.println("p99 (99th percentile) response time: " + calculator.getP99Response());
        System.out.println("min response time (millisecs): " + calculator.getMinResponse());
        System.out.println("max response time (millisecs): " + calculator.getMaxResponse());
    }
}
