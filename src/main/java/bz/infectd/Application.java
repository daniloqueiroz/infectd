package bz.infectd;

import static bz.infectd.Configuration.getConfiguration;
import static java.lang.String.valueOf;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 * 
 */
public class Application {

    public static void main(String[] args) throws Exception {
        Configuration config = getConfiguration();
        System.setProperty("debug", valueOf(config.debugMode()));
        // TODO parse parameters

        if (args.length > 0) {
            config.hostname(args[0]);
        }
        Daemon daemon = new Daemon();
        daemon.boot();
    }
}
