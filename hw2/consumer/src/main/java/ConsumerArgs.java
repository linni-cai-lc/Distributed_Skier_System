public class ConsumerArgs {
    private String hostName;
    private String userName;
    private String password;
    private int maxThreads;

    public ConsumerArgs(String hostName, String userName, String password, int maxThreads) {
        this.hostName = hostName;
        this.userName = userName;
        this.password = password;
        this.maxThreads = maxThreads;
    }

    public String getHostName() {
        return hostName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public int getMaxThreads() {
        return maxThreads;
    }
}
