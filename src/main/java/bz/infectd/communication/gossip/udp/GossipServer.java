package bz.infectd.communication.gossip.udp;

import static bz.infectd.communication.gossip.udp.MessagesTranslation.datagramToGossip;
import static bz.infectd.core.EventLoopWrapper.ioLoop;
import static org.slf4j.LoggerFactory.getLogger;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import bz.infectd.communication.gossip.GossipHandler;
import bz.infectd.communication.gossip.protocol.Messages.Gossip;

/**
 * UDP Server to receive {@link Gossip} messages.
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class GossipServer extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Logger LOG = getLogger(GossipServer.class);
    private final int port;
    private GossipHandler gossipHandler;

    /**
     * @param port
     *            The port this server should listen to.
     */
    public GossipServer(int port, GossipHandler handler) {
        this.port = port;
        this.gossipHandler = handler;
    }

    /**
     * Makes the server start listen to the incoming PORT
     * 
     * @throws InterruptedException
     */
    public void listen() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(ioLoop()).channel(NioDatagramChannel.class).handler(this);
        LOG.info("UDP server listen to port {}", this.port);
        bootstrap.bind(this.port).sync().channel().closeFuture();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel
     * .ChannelHandlerContext, java.lang.Object)
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
        LOG.debug("Message received: {}", packet);
        Gossip message = datagramToGossip(packet);
        this.gossipHandler.add(message);
    }

    /**
     * Just for ad-hoc tests
     */
    public static void main(String[] args) throws Exception {
        new GossipServer(7777, new GossipHandler() {
            @Override
            public void add(Gossip message) {
                LOG.info("Message received: {}", message);
            }
        }).listen();
        ioLoop().awaitTermination(5, TimeUnit.MINUTES);
    }
}
