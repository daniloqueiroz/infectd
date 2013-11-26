package bz.infectd.core;

import static bz.infectd.Configuration.getConfiguration;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Simple wrapper to provide access to the {@link EventLoopGroup} used by this
 * system.
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class EventLoopWrapper {

    private static final EventLoopGroup loop = new NioEventLoopGroup(getConfiguration()
            .threadsCount());

    public static EventLoopGroup systemEventLoop() {
        return loop;
    }
}
