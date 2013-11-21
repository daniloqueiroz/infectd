package bz.infectd.communication.udp;

import static bz.infectd.application.EventLoopWrapper.systemEventLoop;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import bz.infectd.communication.protocol.gossip.P2PProtocol.Gossip;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Server extends SimpleChannelInboundHandler<DatagramPacket> {

    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public void listen() throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(systemEventLoop()).channel(NioDatagramChannel.class).handler(this);
        // TODO remove no await
        bootstrap.bind(this.port).sync().channel().closeFuture().await();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        byte[] array = this.extractBytes(packet);
        Gossip message = Gossip.parseFrom(array);
        System.err.println(">>>>>>>>>>>>>>>>>>>>" + message.getType());
        ctx.close();
    }

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

    public static void main(String[] args) throws Exception {
        new Server(7777).listen();
    }
}
