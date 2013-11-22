package bz.infectd.communication.gossip;

import bz.infectd.communication.gossip.protocol.P2PProtocol;
import bz.infectd.communication.gossip.protocol.P2PProtocol.Gossip;
import bz.infectd.communication.gossip.protocol.P2PProtocol.Gossip.Builder;
import bz.infectd.communication.gossip.protocol.P2PProtocol.Gossip.Type;
import bz.infectd.membership.Heartbeat;

/**
 * Factory for create messages from the model objects.
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class MessageFactory {

    /**
     * Create a {@link Builder} for the {@link Gossip} message with the given
     * {@link Type}
     */
    private static Gossip.Builder createGossipMessageBuilder(Type type) {
        Gossip.Builder builder = Gossip.newBuilder();
        builder.setType(type);
        return builder;
    }

    /**
     * Create a {@link GossipMessage} for the propagate the given
     * {@link Heartbeat}
     * 
     * @param hb
     *            The {@link Heartbeat} to be encapsulate on the
     *            {@link GossipMessage}
     * @return a {@link GossipMessage}
     */
    public static Gossip createMessage(Heartbeat hb) {
        Gossip.Builder builder = createGossipMessageBuilder(Type.HEARTBEAT);
        builder.setHeartbeat(P2PProtocol.Heartbeat.newBuilder().setHeartbeat(hb.clock())
                .setNodeHost(hb.address()).setNodePort(hb.port()));
        return builder.build();
    }
}
