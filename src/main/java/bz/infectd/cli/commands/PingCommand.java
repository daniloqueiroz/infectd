package bz.infectd.cli.commands;

/**
 * A simple ping command to check if the server is up and running. It returns
 * "PONG".
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class PingCommand implements InfectdCommand {

    @Override
    public String execute(String... params) throws InfectdCommandException {
        return "PONG";
    }
}
