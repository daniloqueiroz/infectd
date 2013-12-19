package bz.infectd;

import static bz.infectd.Configuration.getConfiguration;
import static bz.infectd.communication.gossip.MessageFactory.createMessage;
import static bz.infectd.core.EventLoopWrapper.scheduleRecurrentCommand;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import bz.infectd.communication.cli.tcp.CLIServer;
import bz.infectd.communication.gossip.GossipHandler;
import bz.infectd.communication.gossip.protocol.Messages.Gossip;
import bz.infectd.communication.gossip.udp.GossipClient;
import bz.infectd.communication.gossip.udp.GossipServer;
import bz.infectd.core.Clock;
import bz.infectd.journaling.GossipJournalAdapter;
import bz.infectd.journaling.Journal;
import bz.infectd.membership.Heartbeat;
import bz.infectd.membership.HeartbeatMonitor;
import bz.infectd.membership.MembershipBoard;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Daemon {

    private static final Logger logger = getLogger(Daemon.class);

    private Journal journal;
    private HeartbeatMonitor monitor;
    private Clock clock;
    private Configuration config;

    protected Daemon() {
        this.config = getConfiguration();
    }

    /**
     * Boots the Infectd Daemon
     */
    public void boot() throws InterruptedException {
        logger.info("Starting daemon - {}:{}", this.config.hostname(), this.config.networkPort());
        this.journal = this.setupJornal();
        this.monitor = new HeartbeatMonitor(new Heartbeat(this.config.hostname(),
                this.config.networkPort()));
        GossipServer gossipServer = this.setupGossipServer();
        this.broadcastHeartbeat();
        gossipServer.listen();
        new CLIServer(this.config.networkPort()).listen();
    }

    private Journal setupJornal() {
        MembershipBoard board = new MembershipBoard();
        return new Journal(board);
    }

    private GossipServer setupGossipServer() {
        final GossipHandler handler = new GossipJournalAdapter(this.journal);
        GossipServer udpServer = new GossipServer(this.config.networkPort(), new GossipHandler() {
            private transient boolean hasReceivedFirstMessage = false;

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
        if (this.clock == null) {
            this.clock = new Clock(this.journal, this.monitor);
            logger.info("Scheduling clock to happen every {} seconds", this.config.clockInterval());
            scheduleRecurrentCommand(this.clock, this.config.clockInterval());
        }
    }

    private void broadcastHeartbeat() throws InterruptedException {
        GossipClient udpClient = new GossipClient(this.config.networkPort());
        udpClient.send(createMessage(this.monitor.heartbeat()));
    }
}
