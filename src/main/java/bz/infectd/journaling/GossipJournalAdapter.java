package bz.infectd.journaling;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import bz.infectd.communication.gossip.GossipHandler;
import bz.infectd.communication.gossip.protocol.Messages.Gossip;
import bz.infectd.communication.gossip.protocol.Messages.Gossip.Type;
import bz.infectd.core.IllegalMessageException;
import bz.infectd.membership.Heartbeat;

import com.google.inject.Inject;

/**
 * This class is responsible by receiving {@link Gossip} messages and send the
 * respective entries to the {@link Journal}
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class GossipJournalAdapter implements GossipHandler {
    private static final Logger LOG = getLogger(GossipJournalAdapter.class);
    private Journal journal;

    @Inject
    public GossipJournalAdapter(Journal journal) {
        this.journal = journal;
    }

    @Override
    public void add(Gossip message) {
        switch (message.getType().getNumber()) {
        case Type.HEARTBEAT_VALUE:
            Heartbeat hb = this.translate(message.getHeartbeat());
            LOG.info("Heartbeart received: {}", hb);
            this.journal.add(hb);
            break;
        default:
            throw new IllegalMessageException();
        }
    }

    /**
     * Translate a
     * {@link bz.infectd.communication.gossip.protocol.Messages.Heartbeat} to a
     * {@link Heartbeat}
     */
    private Heartbeat translate(
            bz.infectd.communication.gossip.protocol.Messages.Heartbeat heartbeat) {
        Heartbeat hb = new Heartbeat(heartbeat.getNodeHost(), heartbeat.getNodePort());
        hb.clock(heartbeat.getHeartbeat());
        return hb;
    }

}
