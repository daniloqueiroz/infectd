package bz.infectd.cli.commands;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class PingCommand implements InfectdCommand {

    @Override
    public String execute(String... params) throws InfectdCommandException {
        return "PONG";
    }
}
