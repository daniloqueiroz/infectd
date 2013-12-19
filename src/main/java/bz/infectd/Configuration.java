package bz.infectd;

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
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
        return parseInt(this.properties.getProperty("infectd.network.port"));
    }

    /**
     * @return The number of threads to be used by the event loop
     */
    public int ioThreadsCount() {
        return parseInt(this.properties.getProperty("infectd.network.threads"));
    }

    public String hostname() {
        String hostname = this.properties.getProperty("infectd.network.hostname");
        if (hostname == null) {
            hostname = getIPAddress();
        }
        return hostname;
    }

    public void hostname(String hostname) {
        this.properties.setProperty("infectd.network.hostname", hostname);
    }

    /**
     * @return The interval between clock ticks - in seconds
     */
    public int clockInterval() {
        return parseInt(this.properties.getProperty("infectd.clock.interval"));
    }

    /**
     * @return Number of rounds before a member be considered dead
     */
    public int roundsCount() {
        return parseInt(this.properties.getProperty("infectd.rounds_to_death"));
    }

    /**
     * @return
     */
    public int minimunPropagationFactor() {
        return parseInt(this.properties.getProperty("infectd.min_propagation_factor"));
    }

    public float propagationFactor() {
        return parseFloat(this.properties.getProperty("infectd.propagation_factor"));
    }

    private static String getIPAddress() {
        String address = null;
        try {
            address = findInterface();
            for (int i = 0; i < 3 && address == null; i++) {
                Thread.sleep(500);
                address = findInterface();
            }
        } catch (SocketException | InterruptedException e) {
            // ignore
        }
        return (address != null) ? address : "127.0.0.1";
    }

    private static String findInterface() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface netIF = interfaces.nextElement();
            if (!netIF.isLoopback() && netIF.isUp()) {
                Enumeration<InetAddress> addresses = netIF.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address) {
                        return address.getHostAddress();
                    }
                }
            }
        }
        return null;
    }
}
