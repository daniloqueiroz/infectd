package bz.infectd.communication.udp;

import static bz.infectd.application.EventLoopWrapper.systemEventLoop;
import static org.slf4j.LoggerFactory.getLogger;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import bz.infectd.communication.protocol.gossip.P2PProtocol.Gossip;

/**
 * UDP Server to receive {@link Gossip} messages.
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Server extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Logger logger = getLogger(Client.class);
    private final int port;

    /**
     * @param port
     *            The port this server should listen to.
     */
    public Server(int port) {
        this.port = port;
    }

    /**
     * Makes the server start listen to the incoming PORT
     */
    public void listen() throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(systemEventLoop()).channel(NioDatagramChannel.class).handler(this);
        logger.info("UDP server listen to port {}", this.port);
        bootstrap.bind(this.port).sync().channel().closeFuture();
    }

    /* (non-Javadoc)
     * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        this.messageReceived(ctx, packet);
    }

    /**
     * Receives the {@link DatagramPacket} from Netty.
     */
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket packet)
            throws Exception {
        logger.debug("Message received: {}", packet);
        byte[] array = this.extractBytes(packet);
        Gossip message = Gossip.parseFrom(array);
        logger.info("Message received: {}", message);
        // TODO do something with the message
    }

    /**
     * Extracts the byte[] content of the given packet
     */
    private byte[] extractBytes(DatagramPacket packet) {
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

    /**
     * Just for ad-hoc tests
     */
    public static void main(String[] args) throws Exception {
        new Server(7777).listen();
        systemEventLoop().awaitTermination(5, TimeUnit.MINUTES);
    }
}
