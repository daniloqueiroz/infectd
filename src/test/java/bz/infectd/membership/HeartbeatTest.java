package bz.infectd.membership;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class HeartbeatTest {

    private Heartbeat hb;

    @Before
    public void setUp() {
        this.hb = new Heartbeat("some", 0);
    }

    @Test
    public void updatesHeartbeatClock() {
        this.hb.clock(3);
        assertEquals(3, this.hb.clock());
    }

    @Test
    public void doesntUpdateHeartbeatClock() {
        this.hb.clock(3);
        this.hb.clock(2);
        assertEquals(3, this.hb.clock());
    }
}
