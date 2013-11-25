package bz.infectd;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 * 
 */
public class Application {

    public static void main(String[] args) throws Exception {
        // TODO parse parameters
        String address = null;
        if (args.length > 0) {
            address = args[0];
        } else {
            address = getIPAddress();
        }
        Daemon daemon = new Daemon(address);
        daemon.boot();
    }

    private static String getIPAddress() throws InterruptedException, SocketException {
        String address = findInterface();
        for (int i = 0; i < 3 && address == null; i++) {
            Thread.sleep(500);
            address = findInterface();
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
