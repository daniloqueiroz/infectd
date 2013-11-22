package bz.infectd.core;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Simple wrapper to provide access to the {@link EventLoopGroup} used by this
 * system.
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class EventLoopWrapper {

    // TODO make it configurable
    private static final int NUMBER_OF_THREADS = 1;
    private static final EventLoopGroup loop = new NioEventLoopGroup(NUMBER_OF_THREADS);

    public static EventLoopGroup systemEventLoop() {
        return loop;
    }
}
