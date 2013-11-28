package bz.infectd.journaling;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;

import bz.infectd.core.EntriesProcessor;

/**
 * Keep tracks of all the events received that are pending to process.
 * It keeps all the entries on a buffer and on {@link Journal#sync()} it sends
 * them to the fanout and reset the buffer.
 * 
 * All the public methods of this class are thread-safe.
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Journal {

    private static final Logger logger = getLogger(Journal.class);
    private LinkedHashSet<Entry<?>> entries = new LinkedHashSet<>();
    private Lock monitor = new ReentrantLock();
    private EntriesProcessor fanout;

    public Journal(EntriesProcessor fanout) {
        this.fanout = fanout;
    }

    /**
     * Add a new {@link Entry} to the journal buffer.
     */
    public void add(Entry<?> entry) {
        this.monitor.lock();
        logger.debug("Adding new entry {}", entry);
        this.entries.add(entry);
        this.monitor.unlock();
    }

    /**
     * Syncs all the entries on the current Journal buffer and reset the buffer.
     * By sync it means that all the entries are sent to the
     * {@link EntriesProcessor} that is responsible to update the respective
     * data for each entry.
     */
    public void sync() {
        logger.debug("Performing journaling sync");
        Collection<Entry<?>> toSync = this.rotateEntries();
        this.fanout.process(toSync);
    }

    /**
     * Return all the entries on the journal buffer and reset the internal
     * Journal buffer.
     */
    private Collection<Entry<?>> rotateEntries() {
        this.monitor.lock();
        Collection<Entry<?>> current = this.entries();
        this.entries = new LinkedHashSet<>();
        this.monitor.unlock();
        return current;
    }

    protected Collection<Entry<?>> entries() {
        this.monitor.lock();
        try {
            return this.entries;
        } finally {
            this.monitor.unlock();
        }
    }
}
