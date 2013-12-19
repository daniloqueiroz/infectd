package bz.infectd.communication.cli.tcp;

import static bz.infectd.core.EventLoopWrapper.ioLoop;
import static org.slf4j.LoggerFactory.getLogger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import org.slf4j.Logger;

import bz.infectd.communication.cli.protocol.Messages;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 * 
 */
public class CLIServer {

    private static final Logger logger = getLogger(CLIServer.class);
    private final int port;

    public CLIServer(int port) {
        this.port = port;
    }

    public void listen() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(ioLoop()).channel(NioServerSocketChannel.class)
                .childHandler(new ServerInitializer());

        logger.info("TCP server listen to port {}", this.port);
        bootstrap.bind("127.0.0.1", this.port).sync().channel().closeFuture();
    }

    /**
     * Initializes the server handler
     */
    private class ServerInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
            pipeline.addLast("protobufDecoder",
                    new ProtobufDecoder(Messages.Command.getDefaultInstance()));

            pipeline.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
            pipeline.addLast("protobufEncoder", new ProtobufEncoder());
            pipeline.addLast("handler", new CommandHandler());
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8212;
        }
        new CLIServer(port).listen();
    }
}
