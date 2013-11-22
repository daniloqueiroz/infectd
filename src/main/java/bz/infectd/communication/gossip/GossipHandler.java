package bz.infectd.communication.gossip;

import bz.infectd.communication.gossip.protocol.P2PProtocol.Gossip;
import bz.infectd.journaling.Entry;
import bz.infectd.journaling.Journal;

/**
 * Responsible by handling {@link Gossip} messages
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public interface GossipHandler {

    /**
     * Creates a new {@link Entry} from the {@link Gossip} message and add it to
     * the {@link Journal}
     */
    public abstract void addEntry(Gossip message);

}