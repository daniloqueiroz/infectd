package bz.infectd.cli.commands;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class InfectdCommandException extends Exception {

    private static final long serialVersionUID = -157932173086023568L;
    private int exitCode = -1;

    public InfectdCommandException(String message) {
        super(message);
    }

    public InfectdCommandException(Throwable cause) {
        super(cause);
    }

    public InfectdCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @return the exitCode
     */
    public int getExitCode() {
        return this.exitCode;
    }

    /**
     * @param exitCode
     *            the exitCode to set
     */
    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }
}
