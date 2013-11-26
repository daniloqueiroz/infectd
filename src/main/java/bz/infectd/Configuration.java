package bz.infectd;

import static java.lang.Integer.parseInt;
import static java.lang.ClassLoader.getSystemResourceAsStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Configuration {

    private static final String DEFAULT_PROPERTIES = "infectd-defaults.conf";
    private static final Configuration instance = new Configuration();

    public static Configuration getConfiguration() {
        return instance;
    }

    private Properties properties;

    private Configuration() {
        this.properties = new Properties();
        try {
            this.loadConfig(DEFAULT_PROPERTIES);
        } catch (IOException e) {
            throw new ApplicationException("Unable to load default configuration.", e);
        }
    }

    private void loadConfig(String properties) throws IOException {
        try (InputStream in = getSystemResourceAsStream(properties)) {
            this.properties.load(in);
        }
    }

    /**
     * @return The network port to be used
     */
    public int networkPort() {
        return parseInt(this.properties.getProperty("network.port"));
    }

    /**
     * @return The interval between clock ticks - in seconds
     */
    public int clockInterval() {
        return parseInt(this.properties.getProperty("clock.interval"));
    }

    /**
     * @return The number of threads to be used by the event loop
     */
    public int threadsCount() {
        return parseInt(this.properties.getProperty("core.threads"));
    }

    /**
     * @return Number of rounds before a member be considered dead
     */
    public int roundsCount() {
        return parseInt(this.properties.getProperty("membership.rounds_count"));
    }

}
