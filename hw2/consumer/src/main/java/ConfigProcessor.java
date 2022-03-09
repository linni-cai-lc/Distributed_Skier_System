import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigProcessor {
    private static String CONFIG_FILE = "config.properties";

    public static Config processConfig() {
        InputStream inputStream;
        Properties properties = new Properties();
        try {
            ClassLoader classLoader = MultiThreadedConsumer.class.getClassLoader();
            inputStream = classLoader.getResourceAsStream(CONFIG_FILE);
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException("Cannot find the config file.");
            }
            if (properties == null || properties.isEmpty()) {
                throw new IllegalArgumentException("Consumer arg is empty.");
            }
            return new Config(properties.getProperty("host"),
                              properties.getProperty("username"),
                              properties.getProperty("password"),
                              Integer.valueOf(properties.getProperty("maxThreads")));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
