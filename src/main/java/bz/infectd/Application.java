package bz.infectd;

import java.net.UnknownHostException;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 *
 */
public class Application {

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        Daemon.boot();
    }

}
