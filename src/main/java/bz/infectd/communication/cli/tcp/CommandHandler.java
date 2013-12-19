package bz.infectd.communication.cli.tcp;

import bz.infectd.communication.cli.protocol.Messages.Command;
import bz.infectd.communication.cli.protocol.Messages.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 *
 */
public class CommandHandler extends SimpleChannelInboundHandler<Command> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command cmd) throws Exception {
        System.out.println(cmd.getCommand() + " " + cmd.getParams());
        ctx.writeAndFlush(Response.newBuilder().setExitCode(0).setMessage("echo").build());
    }
}
