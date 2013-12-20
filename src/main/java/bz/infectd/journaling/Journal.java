package bz.infectd.journaling;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;

import bz.infectd.communication.gossip.PropagationAgent;
import bz.infectd.membership.Heartbeat;
import bz.infectd.membership.MembershipBoard;

import com.google.inject.Inject;

/**
 * Keep tracks of all the events received that are pending to process. It keeps
 * all the entries on a buffer and on {@link Journal#sync()} it sends them to
 * the fanout and reset the buffer.
 * 
 * All the public methods of this class are thread-safe.
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Journal {

    private static final Logger LOG = getLogger(Journal.class);
    private Lock monitor = new ReentrantLock();
    protected EntriesCollection entries = new EntriesCollection();
    private MembershipBoard board;

    @Inject
    public Journal(MembershipBoard board) {
        this.board = board;
    }

    /**
     * Add a new {@link Entry} to the journal buffer.
     */
    public void add(Heartbeat entry) {
        this.monitor.lock();
        LOG.debug("Adding new entry {}", entry);
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
        LOG.debug("Performing journaling sync");
        EntriesCollection toSync = this.rotateEntries();
        this.process(toSync);
    }

    private void process(EntriesCollection toSync) {
        Collection<Heartbeat> updated = this.board.updateHeartbeats(toSync.heartbeats());
        PropagationAgent<Heartbeat> agent = new PropagationAgent<>(updated, this.board.heartbeats());
        agent.propagate();
    }

    /**
     * Return all the entries on the journal buffer and reset the internal
     * Journal buffer.
     */
    private EntriesCollection rotateEntries() {
        this.monitor.lock();
        EntriesCollection current = this.entries;
        this.entries = new EntriesCollection();
        this.monitor.unlock();
        return current;
    }
}
