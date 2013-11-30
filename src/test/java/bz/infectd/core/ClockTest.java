package bz.infectd.core;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createStrictMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Test;

import bz.infectd.journaling.Journal;
import bz.infectd.membership.Heartbeat;
import bz.infectd.membership.HeartbeatMonitor;

public class ClockTest {

    @Test
    public void ticksClock() {
        Heartbeat hb = createStrictMock(Heartbeat.class);

        HeartbeatMonitor monitor = createStrictMock(HeartbeatMonitor.class);
        Journal journal = createStrictMock(Journal.class);
        Clock clock = new Clock(journal, monitor);

        monitor.pulse();
        expect(monitor.heartbeat()).andReturn(hb);

        journal.add(hb);
        journal.sync();
        replayAll();

        clock.tick();
        verifyAll();
    }
}
