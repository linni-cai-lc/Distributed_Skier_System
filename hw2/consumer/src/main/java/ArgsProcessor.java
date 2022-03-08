import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ArgsProcessor {
    private static String CONFIG_FILE = "config.properties";

    public static ConsumerArgs processArgs() {
        InputStream inputStream;
        Properties properties = new Properties();
        if (properties == null || properties.isEmpty()) {
            throw new IllegalArgumentException("Consumer arg is empty.");
        }
        try {
            ClassLoader classLoader = MultiThreadedConsumer.class.getClassLoader();
            inputStream = classLoader.getResourceAsStream(CONFIG_FILE);
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException("Cannot find the config file.");
            }
            return new ConsumerArgs(properties.getProperty("host"),
                                    properties.getProperty("userName"),
                                    properties.getProperty("password"),
                                    Integer.valueOf(properties.getProperty("maxThreads")));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
