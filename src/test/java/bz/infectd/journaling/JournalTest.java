package bz.infectd.journaling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import bz.infectd.core.EntriesProcessor;

public class JournalTest {

    private Journal journal;
    private EntriesProcessor fanout;

    @Before
    public void setUp() {
        this.fanout = createMock(EntriesProcessor.class);
        this.journal = new Journal(this.fanout);
    }

    @Test
    public void addsAndGetEntries() {
        Entry<?> entry1 = createMock(Entry.class);
        Entry<?> entry2 = createMock(Entry.class);
        this.journal.add(entry1);
        this.journal.add(entry2);
        Iterator<Entry<?>> itr = this.journal.entries().iterator();
        assertEquals(entry1, itr.next());
        assertEquals(entry2, itr.next());
        assertFalse(itr.hasNext());
    }

    @Test
    public void addsNoDuplicates() {
        Entry<?> entry = createMock(Entry.class);
        this.journal.add(entry);
        this.journal.add(entry);
        assertEquals(1, this.journal.entries().size());
    }

    @Test
    public void syncs() {
        for (int i = 0; i < 5; i++) {
            this.journal.add(createMock(Entry.class));
        }
        this.fanout.process(this.journal.entries());
        replayAll();
        Collection<Entry<?>> synced = this.journal.sync();
        verifyAll();
        assertEquals(5, synced.size());
        assertEquals(0, this.journal.entries().size());
    }
}
