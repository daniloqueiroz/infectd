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
import bz.infectd.communication.gossip.udp.GossipClient;
import bz.infectd.membership.Heartbeat;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class PropagationAgent<T extends Propagable> {

    private static final Logger LOG = getLogger(PropagationAgent.class);
    private static final Configuration CONFIG = getConfiguration();
    private Collection<T> data;
    private List<Heartbeat> members;

    /**
     * @param data
     *            The data to be propagate
     * @param members
     *            The current list of members to be used to propagate to - the
     *            propagation agent can use only a subset of this elements.
     */
    public PropagationAgent(Collection<T> data, List<Heartbeat> members) {
        this.members = members;
        this.data = data;
    }

    public void propagate() {
        this.infect(this.selectMembers());
    }

    protected void infect(List<Heartbeat> toInfect) {
        for (Heartbeat member : toInfect) {
            if (!CONFIG.hostname().equals(member.address())) {
                this.sendEntries(member);
            }
        }
    }

    private void sendEntries(Heartbeat member) {
        LOG.info("Propagating {} entries to {}:{}", this.data.size(), member.address(),
                member.port());
        GossipClient client = new GossipClient(member.address(), member.port());
        for (Propagable toPropagate : this.data) {
            try {
                client.send(createMessage(toPropagate));
            } catch (InterruptedException iex) {
                LOG.error("Error propagating message", iex);
            }
        }
    }

    /**
     * Select random members from the heartbeats to propagate. The size of
     * selected members list it's max(X,Y*heartbeats.size()) where Y its a value
     * between 0 and 1;
     */
    protected List<Heartbeat> selectMembers() {
        int amountByFactor = (int) (CONFIG.propagationFactor() * this.members.size());
        int amountToSelect = max(CONFIG.minimunPropagationFactor(), amountByFactor);
        if (this.members.size() <= amountToSelect) {
            return this.members;
        } else {
            shuffle(this.members);
            return this.members.subList(0, amountToSelect);
        }
    }
}
