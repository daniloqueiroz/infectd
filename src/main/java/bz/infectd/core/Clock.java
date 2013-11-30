package bz.infectd.core;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import bz.infectd.journaling.Journal;
import bz.infectd.membership.Heartbeat;
import bz.infectd.membership.HeartbeatMonitor;

/**
 * This class periodically ticks to perform system maintenance task, such as
 * pulsing this system heartbeat and synchronizing the Journal.
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Clock implements Runnable {

    private static final Logger logger = getLogger(Clock.class);
    private Journal journal;
    private HeartbeatMonitor monitor;

    public Clock(Journal journal, HeartbeatMonitor monitor) {
        this.journal = journal;
        this.monitor = monitor;
    }

    public void tick() {
        logger.info("Ticking the clock");
        this.monitor.pulse();
        Heartbeat hb = this.monitor.heartbeat();
        this.journal.add(hb);
        this.journal.sync();
    }

    @Override
    public void run() {
        try {
            this.tick();
        } catch (Exception t) {
            logger.error("Error ticking the clock", t);
        }
    }
}
