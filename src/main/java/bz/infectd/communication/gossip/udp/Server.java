package bz.infectd.communication.gossip.udp;

import static bz.infectd.communication.gossip.udp.MessagesTranslation.datagramToGossip;
import static bz.infectd.core.EventLoopWrapper.systemEventLoop;
import static org.slf4j.LoggerFactory.getLogger;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import bz.infectd.communication.gossip.protocol.P2PProtocol.Gossip;

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
        Gossip message = datagramToGossip(packet);
        logger.info("Message received: {}", message);
        // TODO do something with the message
    }

    /**
     * Just for ad-hoc tests
     */
    public static void main(String[] args) throws Exception {
        new Server(7777).listen();
        systemEventLoop().awaitTermination(5, TimeUnit.MINUTES);
    }
}
