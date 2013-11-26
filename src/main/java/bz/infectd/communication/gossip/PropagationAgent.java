package bz.infectd.communication.gossip;

import static bz.infectd.Configuration.getConfiguration;
import static bz.infectd.communication.gossip.MessageFactory.createMessage;
import static java.lang.Math.max;
import static java.util.Collections.shuffle;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;

import bz.infectd.Configuration;
import bz.infectd.communication.gossip.udp.Client;
import bz.infectd.journaling.Entry;
import bz.infectd.membership.ExtendedHeartbeat;
import bz.infectd.membership.Heartbeat;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class PropagationAgent {

    private static final Logger LOG = getLogger(PropagationAgent.class);
    private static final Configuration CONFIG = getConfiguration();
    private Collection<Entry<?>> entries;
    private List<ExtendedHeartbeat> heartbeats;

    public PropagationAgent(Collection<Entry<?>> entries, List<ExtendedHeartbeat> heartbeats) {
        this.entries = entries;
        this.heartbeats = heartbeats;
    }

    public void propagate() {
        this.infect(this.selectMembers());
    }

    protected void infect(List<ExtendedHeartbeat> toInfect) {
        for (Heartbeat member : toInfect) {
            Client client = new Client(member.address(), member.port());
            for (Entry<?> entry : this.entries) {
                try {
                    client.send(createMessage(entry.unwrap()));
                } catch (InterruptedException iex) {
                    LOG.error("Error propagating message", iex);
                }
            }
        }
    }

    /**
     * Select random members from the heartbeats to propagate. The size of
     * selected members list it's max(X,Y*heartbeats.size()) where Y its a value
     * between 0 and 1;
     */
    protected List<ExtendedHeartbeat> selectMembers() {
        int amountByFactor = (int) (CONFIG.propagationFactor() * this.heartbeats.size());
        int amountToSelect = max(CONFIG.minimunPropagationFactor(), amountByFactor);
        if (this.heartbeats.size() <= amountToSelect) {
            return this.heartbeats;
        } else {
            shuffle(this.heartbeats);
            return this.heartbeats.subList(0, amountToSelect);
        }
    }
}
