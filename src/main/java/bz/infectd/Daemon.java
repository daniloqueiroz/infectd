package bz.infectd;

import static bz.infectd.core.EventLoopWrapper.systemEventLoop;
import io.netty.channel.EventLoopGroup;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import bz.infectd.communication.gossip.GossipHandler;
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
    
    public static final int PORT = 4243;
    private static final long GOSSIP_INTERVAL = 30;

    /**
     * Boots the Infectd Daemon
     */
    public static void boot() throws InterruptedException, UnknownHostException {
        MembershipBoard board = new MembershipBoard();
        EntriesProcessor processor = new EntriesProcessor(board);
        Journal journal = new Journal(processor);
        GossipHandler handler = new GossipToEntryAdapter(journal);
        Server udpServer = new Server(PORT, handler);
        udpServer.listen();
        // TODO get public address
        Heartbeat hb = new Heartbeat(InetAddress.getLocalHost().getHostAddress(), PORT);
        HeartbeatMonitor monitor = new HeartbeatMonitor(hb);
        // Announce itself
        Clock clock = new Clock(journal, monitor);
        EventLoopGroup eventLoop = systemEventLoop();
        eventLoop.scheduleWithFixedDelay(clock, 10, GOSSIP_INTERVAL, TimeUnit.SECONDS);
    }
}
