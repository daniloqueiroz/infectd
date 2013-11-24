package bz.infectd;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 * 
 */
public class Application {

    public static void main(String[] args) throws UnknownHostException, InterruptedException,
            SocketException {
        // TODO parse parameters
        String hostname = getHostname();
        Daemon daemon = new Daemon(hostname);
        daemon.boot();
    }

    private static String getHostname() throws SocketException {
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
        return "localhost";
    }
}
