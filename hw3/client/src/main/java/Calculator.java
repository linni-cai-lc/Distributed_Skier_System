import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Calculator {
    public List<Integer> statistics = new ArrayList<>();
    public long start;
    public long end;

    public Calculator(List<String[]> records, long start, long end) {
        this.start = start;
        this.end = end;

        for (String[] record : records) {
            int latency = Integer.parseInt(record[2]);
            statistics.add(latency);
        }
        Collections.sort(statistics);
    }

    // mean response time (millisecs)
    public int getMeanResponse() {
        if (statistics.size() == 0) {
            return 0;
        }
        int sum = 0;
        for (int i : statistics) {
            sum += i;
        }
        return sum / statistics.size();
    }

    // median response time (millisecs)
    public int getMedianResponse() {
        int size = statistics.size();
        int median = statistics.get(size / 2);
        if (size % 2 == 0) {
            median = (median + statistics.get(size / 2 + 1)) / 2;
        }
        return median;
    }

    // throughput = total number of requests/wall time (requests/second)
    public long getThroughput() {
        return statistics.size() / ((end - start) / 1000);
    }

    // p99 (99th percentile) response time
    public int getP99Response() {
        return statistics.get((int)(statistics.size() * 0.99));
    }

    // min response time (millisecs)
    public int getMinResponse() {
        return statistics.get(0);
    }

    // max response time (millisecs)
    public int getMaxResponse() {
        return statistics.get(statistics.size() - 1);
    }
}
