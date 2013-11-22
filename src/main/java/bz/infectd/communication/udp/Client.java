package bz.infectd.communication.udp;

import static bz.infectd.application.EventLoopWrapper.systemEventLoop;
import static org.slf4j.LoggerFactory.getLogger;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

import org.slf4j.Logger;

import bz.infectd.communication.protocol.gossip.P2PProtocol.Gossip;
import bz.infectd.communication.protocol.gossip.P2PProtocol.Gossip.Type;

/**
 * A client to sent UDP {@link Gossip} messages to a given destination
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Client {

    private static final String BROADCAST_ADDRESS = "255.255.255.255";
    private static final Logger logger = getLogger(Client.class);
    private InetSocketAddress address;

    /**
     * Creates a client to broadcast {@link Gossip} messages over UDP.
     * 
     * @param port
     *            The destination PORT to sent to.
     */
    public Client(int port) {
        this.address = new InetSocketAddress(BROADCAST_ADDRESS, port);
    }

    /**
     * Creates a client to sent a {@link Gossip} message to a given host:port
     * 
     * @param host
     *            The host to send messages to
     * @param port
     *            The destination PORT.
     */
    public Client(String host, int port) {
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
        bootstrap.group(systemEventLoop()).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, false)
                .handler(new LoggingHandler(Client.class, LogLevel.INFO));

        logger.debug("Trying to send {} to {}", message, this.address);
        Channel ch = bootstrap.bind(0).sync().channel();
        ch.writeAndFlush(this.createDatagramPacket(message));
        logger.info("Message {} sent to {}", message, this.address);
        ch.close();
    }

    /**
     * Creates a {@link DatagramPacket} from the given {@link Gossip} message
     */
    private DatagramPacket createDatagramPacket(Gossip message) {
        ByteBuf buf = Unpooled.copiedBuffer(message.toByteArray());
        return new DatagramPacket(buf, this.address);
    }
    
    /**
     * Just for ad-hoc tests
     */
    public static void main(String[] args) throws InterruptedException {
        Client c = new Client("127.0.0.1", 7777);
        Gossip g = Gossip.newBuilder().setType(Type.HEARTBEAT).build();
        c.send(g);
    }
}
