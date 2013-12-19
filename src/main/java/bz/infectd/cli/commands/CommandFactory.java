package bz.infectd.cli.commands;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class CommandFactory {

    public enum CommandMapping {
        PING(PingCommand.class);

        private Class<? extends InfectdCommand> commandClass;

        CommandMapping(Class<? extends InfectdCommand> commandClass) {
            this.commandClass = commandClass;
        }
    }

    public static InfectdCommand create(String commandName) throws IllegalArgumentException {
        CommandMapping mapping = CommandMapping.valueOf(commandName);
        try {
            return mapping.commandClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
