package bz.infectd.communication.protocol;

import static bz.infectd.communication.protocol.MessageFactory.createMessage;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bz.infectd.communication.protocol.gossip.P2PProtocol.Gossip;
import bz.infectd.communication.protocol.gossip.P2PProtocol.Gossip.Type;
import bz.infectd.membership.Heartbeat;

public class MessageFactoryTest {

    @Test
    public void createsGossipMessageForHeartbeat() {
        Heartbeat hb = new Heartbeat("some", 1);
        hb.clock(5);
        Gossip msg = createMessage(hb);
        assertEquals(Type.HEARTBEAT, msg.getType());
        assertEquals("some", msg.getHeartbeat().getNodeHost());
        assertEquals(1, msg.getHeartbeat().getNodePort());
        assertEquals(5, msg.getHeartbeat().getHeartbeat());
    }
}
