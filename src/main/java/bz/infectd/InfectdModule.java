package bz.infectd;

import static bz.infectd.Configuration.getConfiguration;
import bz.infectd.journaling.Journal;
import bz.infectd.membership.Heartbeat;
import bz.infectd.membership.HeartbeatMonitor;
import bz.infectd.membership.MembershipBoard;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class InfectdModule extends AbstractModule {

    private HeartbeatMonitor heartbeatMonitor() {
        Configuration config = getConfiguration();
        return new HeartbeatMonitor(new Heartbeat(config.hostname(), config.networkPort()));
    }

    @Override
    protected void configure() {
        this.bind(MembershipBoard.class).in(Singleton.class);
        this.bind(Journal.class).in(Singleton.class);
        this.bind(HeartbeatMonitor.class).toInstance(this.heartbeatMonitor());
    }
}
