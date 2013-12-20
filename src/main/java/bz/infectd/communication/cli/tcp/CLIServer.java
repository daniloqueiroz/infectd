package bz.infectd.communication.cli.tcp;

import static bz.infectd.core.EventLoopWrapper.ioLoop;
import static org.slf4j.LoggerFactory.getLogger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import org.slf4j.Logger;

import bz.infectd.cli.commands.CommandFactory;
import bz.infectd.communication.cli.CommandAdapter;
import bz.infectd.communication.cli.protocol.Messages;
import bz.infectd.communication.cli.protocol.Messages.Command;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 * 
 */
public class CLIServer {

    private static final Logger LOG = getLogger(CLIServer.class);
    private final int port;
    private CommandAdapter cmdAdapter;

    public CLIServer(int port, CommandFactory factory) {
        this.port = port;
        this.cmdAdapter = new CommandAdapter(factory);
    }

    public void listen() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(ioLoop()).channel(NioServerSocketChannel.class)
                .childHandler(new ServerInitializer());

        LOG.info("TCP server listen to port {}", this.port);
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

    private class CommandHandler extends SimpleChannelInboundHandler<Command> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Command cmd) throws Exception {
            String cmdName = cmd.getCommand();
            String[] args = (cmd.hasParams()) ? cmd.getParams().split(" ") : new String[0];
            ctx.writeAndFlush(CLIServer.this.cmdAdapter.executeCommand(cmdName, args));
        }
    }
}
