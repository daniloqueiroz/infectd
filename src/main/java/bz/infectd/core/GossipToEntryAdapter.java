package bz.infectd.core;

import static bz.infectd.journaling.Entry.Builder.createEntry;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import bz.infectd.communication.gossip.GossipHandler;
import bz.infectd.communication.gossip.protocol.Messages.Gossip;
import bz.infectd.communication.gossip.protocol.Messages.Gossip.Type;
import bz.infectd.journaling.Entry;
import bz.infectd.journaling.Journal;
import bz.infectd.membership.Heartbeat;

/**
 * This class is responsible by receiving {@link Gossip} messages and send the
 * respective entries to the {@link Journal}
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class GossipToEntryAdapter implements GossipHandler {
    private static final Logger logger = getLogger(GossipToEntryAdapter.class);
    private Journal journal;

    public GossipToEntryAdapter(Journal journal) {
        this.journal = journal;
    }

    /* (non-Javadoc)
     * @see bz.infectd.core.GossipHandler#addEntry(bz.infectd.communication.protocol.gossip.P2PProtocol.Gossip)
     */
    @Override
    public void addEntry(Gossip message) {
        Entry<?> entry;
        switch (message.getType().getNumber()) {
        case Type.HEARTBEAT_VALUE:
            Heartbeat hb = this.translate(message.getHeartbeat());
            logger.info("Heartbeart received: {}", hb);
            entry = createEntry(hb);
            break;
        default:
            throw new IllegalMessageException();
        }
        journal.add(entry);
    }

    /**
     * Translate a
     * {@link bz.infectd.communication.gossip.protocol.Messages.Heartbeat} to
     * a {@link Heartbeat}
     */
    private Heartbeat translate(
            bz.infectd.communication.gossip.protocol.Messages.Heartbeat heartbeat) {
        Heartbeat hb = new Heartbeat(heartbeat.getNodeHost(), heartbeat.getNodePort());
        hb.clock(heartbeat.getHeartbeat());
        return hb;
    }

}
