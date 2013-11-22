package bz.infectd.communication.udp;

import static bz.infectd.communication.udp.MessagesTranslation.datagramToGossip;
import static bz.infectd.communication.udp.MessagesTranslation.gossipToDatagram;
import static org.junit.Assert.assertEquals;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

import org.junit.Test;

import bz.infectd.communication.protocol.gossip.P2PProtocol.Gossip;
import bz.infectd.communication.protocol.gossip.P2PProtocol.Gossip.Type;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 * 
 */
public class MessagesTranslationTest {

    @Test
    public void translatesGossipToDatagram() throws InvalidProtocolBufferException {
        Gossip msg = Gossip.newBuilder().setType(Type.HEARTBEAT).build();
        InetSocketAddress address = new InetSocketAddress("localhost", 7777);
        DatagramPacket packet = gossipToDatagram(msg, address);
        assertEquals(address, packet.recipient());
        assertEquals(msg, Gossip.parseFrom(packet.content().array()));
    }

    @Test
    public void translatesDatagramToGossip() throws InvalidProtocolBufferException {
        Gossip msg = Gossip.newBuilder().setType(Type.HEARTBEAT).build();
        InetSocketAddress address = new InetSocketAddress("localhost", 7777);
        DatagramPacket packet = gossipToDatagram(msg, address);

        Gossip other = datagramToGossip(packet);
        assertEquals(msg, other);
    }
}
