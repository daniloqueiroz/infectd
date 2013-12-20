package bz.infectd.communication.cli;

import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import bz.infectd.cli.commands.CommandFactory;
import bz.infectd.cli.commands.InfectdCommand;
import bz.infectd.cli.commands.InfectdCommandException;
import bz.infectd.communication.cli.protocol.Messages.Response;
import bz.infectd.communication.cli.protocol.Messages.Response.Builder;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class CommandAdapter {

    private static final Logger LOG = getLogger(CommandAdapter.class);
    private CommandFactory factory;

    public CommandAdapter(CommandFactory factory) {
        this.factory = factory;
    }

    public Response executeCommand(String cmdName, String[] args) {
        Builder responseBuilder = Response.newBuilder();
        try {
            LOG.info("Trying to execute command {} {}", cmdName, args);
            InfectdCommand command = this.factory.create(cmdName);
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
