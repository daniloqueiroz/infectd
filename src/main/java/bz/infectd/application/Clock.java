package bz.infectd.application;

import static bz.infectd.journaling.Entry.Builder.createEntry;
import bz.infectd.journaling.Journal;
import bz.infectd.membership.Heartbeat;
import bz.infectd.membership.HeartbeatMonitor;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Clock {

    private Journal journal;
    private HeartbeatMonitor monitor;

    public Clock(Journal journal, HeartbeatMonitor monitor) {
        this.journal = journal;
        this.monitor = monitor;
    }

    public void tick() {
        this.monitor.pulse();
        Heartbeat hb = this.monitor.heartbeat();
        this.journal.add(createEntry(hb));
        this.journal.sync();
        // TODO propagate entries to other peers
    }
}
