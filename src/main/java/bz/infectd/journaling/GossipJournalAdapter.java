package bz.infectd.journaling;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import bz.infectd.communication.gossip.GossipHandler;
import bz.infectd.communication.gossip.protocol.Messages.Gossip;
import bz.infectd.communication.gossip.protocol.Messages.Gossip.Type;
import bz.infectd.core.IllegalMessageException;
import bz.infectd.membership.Heartbeat;

/**
 * This class is responsible by receiving {@link Gossip} messages and send the
 * respective entries to the {@link Journal}
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class GossipJournalAdapter implements GossipHandler {
    private static final Logger logger = getLogger(GossipJournalAdapter.class);
    private Journal journal;

    public GossipJournalAdapter(Journal journal) {
        this.journal = journal;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * bz.infectd.core.GossipHandler#addEntry(bz.infectd.communication.protocol
     * .gossip.P2PProtocol.Gossip)
     */
    @Override
    public void add(Gossip message) {
        switch (message.getType().getNumber()) {
        case Type.HEARTBEAT_VALUE:
            Heartbeat hb = this.translate(message.getHeartbeat());
            logger.info("Heartbeart received: {}", hb);
            this.journal.add(hb);
            break;
        default:
            throw new IllegalMessageException();
        }
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
