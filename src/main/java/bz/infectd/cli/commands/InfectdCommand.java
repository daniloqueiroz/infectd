package bz.infectd.cli.commands;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public interface InfectdCommand {

    /**
     * Execute the command with the given params.
     * 
     * @param params The args for the command.
     * @return The output/response of the command
     */
    public String execute(String ... params) throws InfectdCommandException;
}
