package bz.infectd;

import static bz.infectd.communication.gossip.MessageFactory.createMessage;
import static bz.infectd.core.EventLoopWrapper.systemEventLoop;
import static org.slf4j.LoggerFactory.getLogger;
import io.netty.channel.EventLoopGroup;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import bz.infectd.communication.gossip.GossipHandler;
import bz.infectd.communication.gossip.protocol.Messages.Gossip;
import bz.infectd.communication.gossip.udp.Client;
import bz.infectd.communication.gossip.udp.Server;
import bz.infectd.core.Clock;
import bz.infectd.core.EntriesProcessor;
import bz.infectd.core.GossipToEntryAdapter;
import bz.infectd.journaling.Journal;
import bz.infectd.membership.Heartbeat;
import bz.infectd.membership.HeartbeatMonitor;
import bz.infectd.membership.MembershipBoard;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Daemon {

    public static final int PORT = 8212; // TODO config
    private static final long GOSSIP_INTERVAL = 30; // TODO config
    private static final Logger logger = getLogger(Daemon.class);
    private Journal journal;
    private HeartbeatMonitor monitor;
    private Server server;
    private String hostname;
    private Clock clock;
    private int port;

    protected Daemon(String hostname) {
        this.hostname = hostname;
        this.port = PORT;
    }

    /**
     * Boots the Infectd Daemon
     */
    public void boot() throws InterruptedException {
        logger.info("Starting daemon - {}:{}", this.hostname, this.port);
        this.journal = this.setupJornal();
        this.monitor = new HeartbeatMonitor(new Heartbeat(this.hostname, this.port));
        this.server = this.setupServer();
        this.broadcastHeartbeat();
        this.server.listen();
    }

    private Journal setupJornal() {
        MembershipBoard board = new MembershipBoard();
        EntriesProcessor processor = new EntriesProcessor(board);
        return new Journal(processor);
    }

    private Server setupServer() {
        final GossipHandler handler = new GossipToEntryAdapter(this.journal);
        Server udpServer = new Server(PORT, new GossipHandler() {
            private transient boolean hasReceivedFirstMessage = false;

            @Override
            public void addEntry(Gossip message) {
                if (!this.hasReceivedFirstMessage) {
                    // Not thread-safe, but we rely on the setupClock that is
                    this.hasReceivedFirstMessage = true;
                    Daemon.this.setupClock();
                }
                handler.addEntry(message);
            }
        });
        return udpServer;
    }

    private synchronized void setupClock() {
        if (this.clock == null) {
            this.clock = new Clock(this.journal, this.monitor);
            EventLoopGroup eventLoop = systemEventLoop();
            eventLoop.scheduleWithFixedDelay(this.clock, 0, GOSSIP_INTERVAL, TimeUnit.SECONDS);
        }
    }

    private void broadcastHeartbeat() throws InterruptedException {
        Client udpClient = new Client(this.port);
        udpClient.send(createMessage(this.monitor.heartbeat()));
    }
}
