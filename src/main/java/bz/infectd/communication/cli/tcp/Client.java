package bz.infectd.communication.cli.tcp;

import static bz.infectd.core.EventLoopWrapper.ioLoop;
import static org.slf4j.LoggerFactory.getLogger;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;

import bz.infectd.communication.cli.protocol.Messages;
import bz.infectd.communication.cli.protocol.Messages.Command;
import bz.infectd.communication.cli.protocol.Messages.Response;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 * 
 */
public class Client {

    private static final Logger logger = getLogger(Client.class);
    private final String host;
    private final int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Response send(Command cmd) throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(ioLoop()).channel(NioSocketChannel.class).handler(new ClientInitializer());

        Channel ch = bootstrap.connect(this.host, this.port).sync().channel();
        try {
            ClientHandler client = ch.pipeline().get(ClientHandler.class);
            return client.sendCommand(cmd);
        } finally {
            ch.close();
        }
    }

    private class ClientInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
            pipeline.addLast("protobufDecoder",
                    new ProtobufDecoder(Messages.Response.getDefaultInstance()));

            pipeline.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
            pipeline.addLast("protobufEncoder", new ProtobufEncoder());

            pipeline.addLast("handler", new ClientHandler());
        }
    }

    private class ClientHandler extends SimpleChannelInboundHandler<Response> {
        private volatile Channel channel;
        private Response response = null;
        private volatile CountDownLatch lock;

        public ClientHandler() {
            super(false);
        }

        public Response sendCommand(Command command) throws InterruptedException {
            this.lock = new CountDownLatch(1);
            this.channel.writeAndFlush(command);
            this.lock.await();
            return this.response;
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            this.channel = ctx.channel();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            logger.error("Unexpected exception from downstream.", cause);
            ctx.close();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Response msg) throws Exception {
            this.response = msg;
            this.lock.countDown();
        }
    }

    public static void main(String[] args) throws Exception {
        // Parse options.
        String host = "127.0.0.1";
        int port = 8212;
        Command cmd = Command.newBuilder().setCommand("echo").setParams("lalal").build();
        Response resp = new Client(host, port).send(cmd);
        if (resp.getExitCode() == 0) {
            System.out.println(resp.getMessage());
        } else {
            System.err.println(resp.getMessage());
        }
        System.exit(resp.getExitCode());
    }

}
