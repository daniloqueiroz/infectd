package bz.infectd.application;

import static bz.infectd.journaling.Entry.Builder.createEntry;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createStrictMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import bz.infectd.journaling.Entry;
import bz.infectd.journaling.Journal;
import bz.infectd.membership.Heartbeat;
import bz.infectd.membership.HeartbeatMonitor;

public class ClockTest {

    @Test
    public void ticksClock() {
        Heartbeat hb = createStrictMock(Heartbeat.class);
        Entry<?> entry = createEntry(hb);
        Collection<Entry<?>> entries = new LinkedList<>();
        entries.add(entry);

        HeartbeatMonitor monitor = createStrictMock(HeartbeatMonitor.class);
        Journal journal = createStrictMock(Journal.class);
        Clock clock = new Clock(journal, monitor);

        monitor.pulse();
        expect(monitor.heartbeat()).andReturn(hb);

        journal.add(eq(entry));
        expect(journal.sync()).andReturn(entries);
        replayAll();

        clock.tick();
        verifyAll();
    }
}
