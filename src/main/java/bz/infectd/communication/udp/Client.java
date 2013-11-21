package bz.infectd.communication.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

import bz.infectd.communication.protocol.gossip.P2PProtocol.Gossip;
import bz.infectd.communication.protocol.gossip.P2PProtocol.Gossip.Type;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Client {

    private InetSocketAddress address;

    public Client(String host, int port) {
        this.address = new InetSocketAddress(host, port);
    }

    public void send(Gossip message) throws Exception {
        // TODO remove
        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, false)
                .handler(new LoggingHandler(Client.class, LogLevel.INFO));

        Channel ch = bootstrap.bind(0).sync().channel();

        ch.writeAndFlush(this.createDatagramPacket(message));
        ch.close();

        // TODO remove
        group.shutdownGracefully();
    }

    private DatagramPacket createDatagramPacket(Gossip message) {
        ByteBuf buf = Unpooled.copiedBuffer(message.toByteArray());
        return new DatagramPacket(buf, this.address);
    }

    public static void main(String[] args) throws Exception {
        Client m = new Client("127.0.0.1", 7777);
        Gossip msg = Gossip.newBuilder().setType(Type.HEARTBEAT).build();
        m.send(msg);
    }
}
