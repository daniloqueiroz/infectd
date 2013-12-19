package bz.infectd.communication.cli;

import static java.lang.String.format;
import static bz.infectd.cli.commands.CommandFactory.create;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import bz.infectd.cli.commands.InfectdCommand;
import bz.infectd.cli.commands.InfectdCommandException;
import bz.infectd.communication.cli.protocol.Messages.Command;
import bz.infectd.communication.cli.protocol.Messages.Response;
import bz.infectd.communication.cli.protocol.Messages.Response.Builder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class CommandHandler extends SimpleChannelInboundHandler<Command> {

    private static final Logger LOG = getLogger(CommandHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command cmd) throws Exception {
        String cmdName = cmd.getCommand();
        String[] args = (cmd.hasParams()) ? cmd.getParams().split(" ") : new String[0];

        Response resp = executeCommand(cmdName, args);

        ctx.writeAndFlush(resp);
    }

    private Response executeCommand(String cmdName, String[] args) {
        Builder responseBuilder = Response.newBuilder();
        try {
            LOG.info("Trying to execute command %s - %s", cmdName, args);
            InfectdCommand command = create(cmdName);
            responseBuilder.setMessage(command.execute(args)).setExitCode(0);
        } catch (InfectdCommandException ex) {
            LOG.error(format("Error executing command %s - %s", cmdName, args), ex);
            responseBuilder.setMessage(ex.getLocalizedMessage()).setExitCode(ex.getExitCode());
        } catch (Exception ex) {
            LOG.error(format("Error executing command %s - %s", cmdName, args), ex);
            responseBuilder.setMessage(ex.getLocalizedMessage()).setExitCode(-1);
        }
        return responseBuilder.build();
    }
}
