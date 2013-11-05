package bz.infectd.membership;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HeartbeatMonitorTest {

    @Test
    public void updatesHeartbeat() {
        Heartbeat hb = new Heartbeat();
        HeartbeatMonitor heartMonitor = new HeartbeatMonitor(hb);
        heartMonitor.pulse();
        assertEquals(2, hb.clock());
    }

}
