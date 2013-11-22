package bz.infectd.communication.gossip.udp;

import java.net.InetSocketAddress;

import com.google.protobuf.InvalidProtocolBufferException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import bz.infectd.communication.gossip.protocol.P2PProtocol.Gossip;

/**
 * Translate {@link Gossip} message to {@link DatagramPacket} and vice-versa
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class MessagesTranslation {

    /**
     * Translates the given {@link Gossip} and {@link InetSocketAddress} into a
     * {@link DatagramPacket}
     * 
     * @param message
     *            The message to be translated to a {@link DatagramPacket}
     * @param destination
     *            The destination for this packet
     */
    public static DatagramPacket gossipToDatagram(Gossip message, InetSocketAddress destination) {
        ByteBuf buf = Unpooled.copiedBuffer(message.toByteArray());
        return new DatagramPacket(buf, destination);
    }

    /**
     * Translate the given {@link DatagramPacket} into a {@link Gossip}
     * 
     * @param packet
     *            The packet to be translated.
     * @throws InvalidProtocolBufferException
     */
    public static Gossip datagramToGossip(DatagramPacket packet)
            throws InvalidProtocolBufferException {
        byte[] array = extractBytes(packet);
        return Gossip.parseFrom(array);
    }

    /**
     * Extracts the byte[] content of the given packet
     */
    private static byte[] extractBytes(DatagramPacket packet) {
        ByteBuf buf = packet.content();
        final byte[] array;
        final int length = buf.readableBytes();

        if (buf.hasArray()) {
            array = buf.array();
        } else {
            array = new byte[length];
            buf.getBytes(buf.readerIndex(), array, 0, length);
        }
        return array;
    }
}
