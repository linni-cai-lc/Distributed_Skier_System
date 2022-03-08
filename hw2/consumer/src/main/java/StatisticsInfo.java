public class StatisticsInfo {
    private String url;
    private String operation;
    private int mean;
    private int max;

    public StatisticsInfo(String url, String operation, int mean, int max) {
        this.url = url;
        this.operation = operation;
        this.mean = mean;
        this.max = max;
    }

    public String getUrl() {
        return url;
    }

    public String getOperation() {
        return operation;
    }

    public int getMean() {
        return mean;
    }

    public int getMax() {
        return max;
    }
}
