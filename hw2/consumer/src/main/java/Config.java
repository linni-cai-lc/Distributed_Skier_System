public class Config {
    private String host;
    private String username;
    private String password;
    private int maxThreads;

    public Config(String host, String username, String password, int maxThreads) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.maxThreads = maxThreads;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getMaxThreads() {
        return maxThreads;
    }
}
