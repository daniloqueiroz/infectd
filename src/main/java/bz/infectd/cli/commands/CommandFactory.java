package bz.infectd.cli.commands;

import com.google.inject.Injector;

/**
 * Simple factory for the {@link InfectdCommand} using the
 * {@link CommandMapping}
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class CommandFactory {

    /**
     * Maps a command with the enum name to a {@link InfectdCommand} class.
     */
    public enum CommandMapping {
        PING(PingCommand.class);

        private Class<? extends InfectdCommand> commandClass;

        CommandMapping(Class<? extends InfectdCommand> commandClass) {
            this.commandClass = commandClass;
        }
    }

    private Injector injector;
    
    public CommandFactory(Injector injector) {
        this.injector = injector;
    }

    /**
     * Create the {@link InfectdCommand} with the given commandName - based on
     * the {@link CommandMapping}.
     * 
     * @param commandName
     *            The name of the {@link CommandMapping} - case insensitive.
     * @return The {@link InfectdCommand}
     * @throws IllegalArgumentException
     *             If there's no command with the given name
     */
    public InfectdCommand create(String commandName) throws IllegalArgumentException {
        CommandMapping mapping = CommandMapping.valueOf(commandName.toUpperCase());
        return this.injector.getInstance(mapping.commandClass);
    }
}
