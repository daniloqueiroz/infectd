package bz.infectd.communication.gossip.udp;

import static bz.infectd.communication.gossip.udp.MessagesTranslation.gossipToDatagram;
import static bz.infectd.core.EventLoopWrapper.ioLoop;
import static org.slf4j.LoggerFactory.getLogger;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

import org.slf4j.Logger;

import bz.infectd.communication.gossip.protocol.Messages.Gossip;
import bz.infectd.communication.gossip.protocol.Messages.Gossip.Type;

/**
 * A client to sent UDP {@link Gossip} messages to a given destination
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class GossipClient {

    private static final String BROADCAST_ADDRESS = "255.255.255.255";
    private static final Logger LOG = getLogger(GossipClient.class);
    private InetSocketAddress address;
    private boolean isBroadcast = false;

    /**
     * Creates a client to broadcast {@link Gossip} messages over UDP.
     * 
     * @param port
     *            The destination PORT to sent to.
     */
    public GossipClient(int port) {
        this.address = new InetSocketAddress(BROADCAST_ADDRESS, port);
        this.isBroadcast = true;
    }

    /**
     * Creates a client to sent a {@link Gossip} message to a given host:port
     * 
     * @param host
     *            The host to send messages to
     * @param port
     *            The destination PORT.
     */
    public GossipClient(String host, int port) {
        this.address = new InetSocketAddress(host, port);
    }

    /**
     * Sends the given message.
     * 
     * @param message
     *            The message to be sent.
     * @throws InterruptedException
     */
    public void send(Gossip message) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(ioLoop()).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, this.isBroadcast)
                .handler(new LoggingHandler(GossipClient.class, LogLevel.DEBUG));

        Channel ch = bootstrap.bind(0).sync().channel();
        ch.writeAndFlush(gossipToDatagram(message, this.address));
        LOG.info("Message sent to {}: {}", this.address, message.getType());
        ch.close();
    }

    /**
     * Just for ad-hoc tests
     */
    public static void main(String[] args) throws InterruptedException {
        GossipClient c = new GossipClient("127.0.0.1", 7777);
        Gossip g = Gossip.newBuilder().setType(Type.HEARTBEAT).build();
        c.send(g);
    }
}
