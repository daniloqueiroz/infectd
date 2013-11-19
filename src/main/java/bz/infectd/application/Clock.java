package bz.infectd.application;

import static bz.infectd.journaling.Entry.Builder.createEntry;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import bz.infectd.journaling.Journal;
import bz.infectd.membership.Heartbeat;
import bz.infectd.membership.HeartbeatMonitor;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Clock {

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
        this.journal.add(createEntry(hb));
        this.journal.sync();
        // TODO propagate entries to other peers
    }
}
