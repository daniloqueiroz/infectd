package bz.infectd;

import static bz.infectd.Configuration.getConfiguration;
import static bz.infectd.communication.gossip.MessageFactory.createMessage;
import static bz.infectd.core.EventLoopWrapper.scheduleRecurrentCommand;
import static com.google.inject.Guice.createInjector;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import bz.infectd.cli.commands.CommandFactory;
import bz.infectd.communication.cli.tcp.CLIServer;
import bz.infectd.communication.gossip.GossipHandler;
import bz.infectd.communication.gossip.protocol.Messages.Gossip;
import bz.infectd.communication.gossip.udp.GossipClient;
import bz.infectd.communication.gossip.udp.GossipServer;
import bz.infectd.core.Clock;
import bz.infectd.journaling.GossipJournalAdapter;
import bz.infectd.membership.Heartbeat;
import bz.infectd.membership.HeartbeatMonitor;

import com.google.inject.Injector;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Daemon {

    private static final Logger logger = getLogger(Daemon.class);

    private Configuration config;
    private Injector injector;
    private volatile boolean hasStartedClock = false;

    protected Daemon() {
        this.config = getConfiguration();
        this.injector = createInjector(new InfectdModule());
    }

    /**
     * Boots the Infectd Daemon
     */
    public void boot() throws InterruptedException {
        logger.info("Starting daemon - {}:{}", this.config.hostname(), this.config.networkPort());
        this.broadcastHeartbeat();
        this.setupGossipServer().listen();
        new CLIServer(this.config.networkPort(), new CommandFactory(this.injector)).listen();
    }

    private GossipServer setupGossipServer() {
        final GossipHandler handler = this.injector.getInstance(GossipJournalAdapter.class);
        GossipServer udpServer = new GossipServer(this.config.networkPort(), new GossipHandler() {
            private volatile boolean hasReceivedFirstMessage = false;

            @Override
            public void add(Gossip message) {
                if (!this.hasReceivedFirstMessage) {
                    // Not thread-safe, but we rely on the setupClock that is
                    this.hasReceivedFirstMessage = true;
                    Daemon.this.setupClock();
                }
                handler.add(message);
            }
        });
        return udpServer;
    }

    private synchronized void setupClock() {
        if (!this.hasStartedClock) {
            Clock clock = this.injector.getInstance(Clock.class);
            logger.info("Scheduling clock to happen every {} seconds", this.config.clockInterval());
            scheduleRecurrentCommand(clock, this.config.clockInterval());
            this.hasStartedClock = false;
        }
    }

    private void broadcastHeartbeat() throws InterruptedException {
        Heartbeat hb = this.injector.getInstance(HeartbeatMonitor.class).heartbeat();
        GossipClient udpClient = new GossipClient(this.config.networkPort());
        udpClient.send(createMessage(hb));
    }
}
