package bz.infectd.journaling;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.LinkedList;

import org.slf4j.Logger;

import bz.infectd.membership.Heartbeat;
import bz.infectd.membership.MembershipBoard;

/**
 * Kill this class?!
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class EntriesProcessor {

    private static final Logger logger = getLogger(EntriesProcessor.class);
    private MembershipBoard membersBoard;

    public EntriesProcessor(MembershipBoard board) {
        this.membersBoard = board;
    }

    public void process(Collection<Entry<?>> entries) {
        logger.info("Processing %s entries", entries.size());
        ContentsManager contents = new ContentsManager();
        for (Entry<?> entry : entries) {
            contents.add(entry);
        }

        this.membersBoard.updateHeartbeats(contents.heartbeats);
    }

    private class ContentsManager {
        final Collection<Heartbeat> heartbeats = new LinkedList<>();

        public void add(Entry<?> entry) {
            Object content = entry.unwrap();
            if (content instanceof Heartbeat) {
                this.heartbeats.add((Heartbeat) content);
            }
        }
    }
}
