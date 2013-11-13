package bz.infectd.membership;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ExtendedHeartbeatTest {

    private ExtendedHeartbeat hb;

    @Before
    public void setUp() {
        this.hb = new ExtendedHeartbeat(null, 0, 2);
    }

    @Test
    public void updatesHeartbeatClock() {
        this.hb.clock(3);
        assertEquals(3, this.hb.clock());
    }

    @Test
    public void doesntUpdateHeartbeatClock() {
        this.hb.clock(1);
        assertEquals(2, this.hb.clock());
    }

    @Test
    public void countsNotSeenRounds() {
        this.hb.markMissing();
        assertEquals(1, this.hb.missingRounds());
    }

    @Test
    public void resetsMissingCountWhenUpdateClock() {
        this.hb.markMissing();
        this.hb.markMissing();
        this.hb.clock(3);
        assertEquals(0, this.hb.missingRounds());
    }

    @Test
    public void doesntResetMissingCountWhenUpdateClock() {
        this.hb.markMissing();
        this.hb.markMissing();
        this.hb.clock(1);
        assertEquals(2, this.hb.missingRounds());
    }

    @Test
    public void hasChanged() {
        assertTrue(this.hb.hasChanged());
    }

    @Test
    public void marksChangedWhenClockUpdated() {
        this.hb.markNotChanged();
        this.hb.clock(3);
        assertTrue(this.hb.hasChanged());
    }

    @Test
    public void keepsNotChangedWhenClockNotUpdated() {
        this.hb.markNotChanged();
        this.hb.clock(1);
        assertFalse(this.hb.hasChanged());
    }
}
