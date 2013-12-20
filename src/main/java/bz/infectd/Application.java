package bz.infectd;

import static java.lang.String.valueOf;
import static org.slf4j.LoggerFactory.getLogger;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;

import bz.infectd.communication.cli.protocol.Messages.Command;
import bz.infectd.communication.cli.protocol.Messages.Response;
import bz.infectd.communication.cli.tcp.CLIClient;

/**
 * Main entry point for Infectd App.
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Application {

    private static final String VERSION = "1.0.0";
    private static final String URL = "http://github.com/daniloqueiroz/infectd";

    /**
     * Main method.
     */
    public static void main(String[] args) throws Exception {
        Application app = new Application();
        CmdLineParser parser = new CmdLineParser(app);
        parser.parseArgument(args);
        if (!app.quiet) {
            printBanner();
        }

        if (app.help) {
            parser.printUsage(System.out);
        } else {
            app.start();
        }
    }

    private static void printBanner() {
        System.out.printf("Infectd - version %s \n%s\n\n", VERSION, URL);
    }

    // Application object attributes and methods

    @Option(name = "--debug", usage = "Enable debug mode - log out put to console and debug level.")
    private boolean debugMode = false;

    @Option(name = "--ip", usage = "Bind the server to the given ip address - only with --server.")
    private String ip = null;

    @Option(name = "--server", usage = "Runs the infectd in server mode.")
    private boolean serverMode = false;

    @Option(name = "--help", usage = "Show help message.")
    private boolean help = false;

    @Option(name = "--quiet", usage = "Print only the result of the command.")
    private boolean quiet = false;

    @Argument
    private List<String> arguments = new ArrayList<>();

    private Configuration config = Configuration.getConfiguration();

    private Logger logger;

    /**
     * Entry point to start/run the application
     */
    public void start() {
        this.configure();

        if (this.serverMode) {
            this.runDaemon();
        } else if (this.arguments.size() > 0) {
            this.runClient();
        } else {
            System.err.printf("Wrong usage, try using --help to see available options/commands;\n");
            System.exit(-1);
        }
    }

    /**
     * Configures the Application
     */
    private void configure() {
        System.setProperty("debug_mode", valueOf(this.debugMode));
        System.setProperty("server_mode", valueOf(this.serverMode));
        this.logger = getLogger(this.getClass());
        if (this.ip != null) {
            this.config.hostname(this.ip);
        }
    }

    /**
     * Runs infectd Daemon
     */
    private void runDaemon() {
        Daemon daemon = new Daemon();
        try {
            daemon.boot();
            System.out.printf("Server is running... \nPress [ctrl+c] to stop server\n\n");
        } catch (InterruptedException e) {
            this.logger.error("Error running server", e);
            System.err.println("Error running server - check log file for more details");
            System.exit(-1);
        }
    }

    /**
     * Runs infectd client to communicate with the server
     */
    private void runClient() {
        String command = this.arguments.remove(0);
        StringBuilder buf = new StringBuilder();
        for (String s : this.arguments) {
            buf.append(s);
            buf.append(' ');
        }
        Command cmd = Command.newBuilder().setCommand(command).setParams(buf.toString()).build();
        CLIClient client = new CLIClient("127.0.0.1", this.config.networkPort());
        Response resp = null;
        try {
            resp = client.send(cmd);
        } catch (SocketException | InterruptedException e) {
            this.logger.error("Error running clien", e);
            System.err.println("Error running client - check log file for more details");
            System.exit(-1);

        }
        String pattern = "Server>\n%s\n";
        if (this.quiet) {
            pattern = "%s\n";
        }
        if (resp.getExitCode() == 0) {
            System.out.printf(pattern, resp.getMessage());
        } else {
            System.err.printf(pattern, String.format("Error: %s", resp.getMessage()));
        }
        System.exit(resp.getExitCode());
    }
}
