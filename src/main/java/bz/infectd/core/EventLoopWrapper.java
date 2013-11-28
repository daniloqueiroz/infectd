package bz.infectd.core;

import static bz.infectd.Configuration.getConfiguration;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Simple wrapper to provide access to the {@link EventLoopGroup} used by this
 * system.
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class EventLoopWrapper {

    private static final ScheduledExecutorService eventLoop = new ScheduledThreadPoolExecutor(1);
    private static final EventLoopGroup ioLoop = new NioEventLoopGroup(getConfiguration()
            .ioThreadsCount());

    /**
     * Schedules the given command to execute recurrently with a fixed delay in
     * seconds.
     * 
     * @param command
     *            The command to be executed periodically.
     * @param delay
     *            The delay, in seconds, between executions
     */
    public static void scheduleRecurrentCommand(Runnable command, long delay) {
        eventLoop.scheduleWithFixedDelay(command, delay, delay, TimeUnit.SECONDS);
    }

    public static EventLoopGroup ioLoop() {
        return ioLoop;
    }
}
