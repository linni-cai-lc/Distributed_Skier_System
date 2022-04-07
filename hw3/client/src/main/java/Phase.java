import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import org.apache.commons.lang3.concurrent.EventCountCircuitBreaker;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Phase {
    public static final int MIN_WAIT_TIME = 0;
    public static final int MAX_WAIT_TIME = 10;
    public static final Integer RESORT_ID = 111;
    public static final String SEASON_ID = "WINTER";
    public static final String DAY_ID = "112";
    public static final String POST = "POST";
    private static final int MAX_TRY_COUNT = 5;

    public int numThreads;
    public int numSkiers;
    public int startTime;
    public int endTime;
    public int numLifts;
    public int numPosts;
    public AtomicInteger numSuccessReq;
    public AtomicInteger numUnsuccessReq;
    public CountDownLatch totalCompleted;
    public CountDownLatch nextPhaseCompleted;
    public SkiersApi skiersApi;
    public List<String[]> records = new ArrayList<>();
    private EventCountCircuitBreaker breaker;
    private int OPENING_THRESHOLD = 10;

    public Phase(int numThreads, int numSkiers, int startTime, int endTime, int numLifts,
                 int numPosts, AtomicInteger numSuccessReq, AtomicInteger numUnsuccessReq,
                 CountDownLatch totalCompleted, CountDownLatch nextPhaseCompleted, SkiersApi skiersApi) {
        this.numThreads = numThreads;
        this.numSkiers = numSkiers;
        this.startTime = startTime;
        this.endTime = endTime;
        this.numLifts = numLifts;
        this.numPosts = numPosts;
        this.numSuccessReq = numSuccessReq;
        this.numUnsuccessReq = numUnsuccessReq;
        this.totalCompleted = totalCompleted;
        this.nextPhaseCompleted = nextPhaseCompleted;
        this.skiersApi = skiersApi;
    }

    private int getRandomSkierId(int threadIndex) {
        int numSkiersPerThread = numSkiers / numThreads;
        // threadIndex 0, numSkiersPerThread 100
        // 100 * 0 + 1, 100 * 1 -> 1, 100
        // threadIndex 1, numSkiersPerThread 100
        // 100 * 1 + 1, 100 * 2 -> 101, 200
        return getRandomNumber(numSkiersPerThread * threadIndex + 1, numSkiersPerThread * (threadIndex + 1));
    }

    private int getRandomLiftId() {
        return getRandomNumber(CommandLineParser.MIN_NUM_LIFTS, numLifts);
    }

    private int getRandomTime() {
        return getRandomNumber(startTime, endTime);
    }

    private int getRandomWaitTime() {
        return getRandomNumber(MIN_WAIT_TIME, MAX_WAIT_TIME);
    }

    private int getRandomNumber(int minNum, int maxNum) {
        return (int)(Math.random() * (maxNum - minNum + 1)) + minNum;
    }

    public void start() {
        for (int i = 0; i < numThreads; i++) {
            Runnable runnable = createRunnable(i);
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    private LiftRide createLiftRide() {
        LiftRide liftRide = new LiftRide();
        liftRide.setLiftID(getRandomLiftId());
        liftRide.setTime(getRandomTime());
        liftRide.setWaitTime(getRandomWaitTime());
        return liftRide;
    }

    public Runnable createRunnable(final int threadIndex) {
        breaker = new EventCountCircuitBreaker(1000, 1000, TimeUnit.MILLISECONDS, 800, 2, TimeUnit.MILLISECONDS);

        return new Runnable() {
            @Override
            public void run() {
                // POST
                AtomicInteger currentNumPosts = new AtomicInteger(0);
                while (currentNumPosts.get() < numPosts) {
                    String statusCode = "N/A";
                    int skierId = getRandomSkierId(threadIndex);
//                    System.out.println(String.format("------- THREAD: %d, POST: %d, ID: %d ---------", threadIndex, i, skierId));
                    LiftRide liftRide = createLiftRide();

                    long start = -1;
                    long end = -1;
                    boolean successFlag = false;
                    if (breaker.incrementAndCheckState()) {
                        for (int j = 0; j < MAX_TRY_COUNT; j++) {
                            try {
                                start = System.currentTimeMillis();
                                ApiResponse<Void> res = skiersApi.writeNewLiftRideWithHttpInfo(liftRide, RESORT_ID, SEASON_ID, DAY_ID, skierId, liftRide.getLiftID(), liftRide.getTime());
                                end = System.currentTimeMillis();

                                statusCode = String.valueOf(res.getStatusCode());
                                if (res.getStatusCode() == HttpServletResponse.SC_CREATED) {
                                    successFlag = true;
                                    break;
                                } else {
                                    System.out.println(String.format("ERROR: %d time POST Failure with invalid status", j + 1));
                                }
                            } catch (ApiException e) {
                                System.out.println(String.format("ERROR: %d time POST Failure with API Exception", j + 1));
                            }
                        }

                        if (successFlag) {
                            numSuccessReq.incrementAndGet();
                        } else {
                            numUnsuccessReq.incrementAndGet();
                        }
                        currentNumPosts.incrementAndGet();

                        long duration = end - start;
                        //   0            1                      2        3
                        // {start time, request type (ie POST), latency, response code}
                        String[] record = {String.valueOf(start), POST, String.valueOf(duration), statusCode};
                        records.add(record);
                    }
                }
//                if (currentNumPosts)
                totalCompleted.countDown();
                if (nextPhaseCompleted != null) {
                    nextPhaseCompleted.countDown();
                }
            }
        };
    }
}
