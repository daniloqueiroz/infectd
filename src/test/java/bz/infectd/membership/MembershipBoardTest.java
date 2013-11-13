package bz.infectd.membership;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class MembershipBoardTest {
    private MembershipBoard board;

    @Before
    public void setUp() {
        this.board = new MembershipBoard();
    }

    @Test
    public void updatesHeartbeats() {
        Heartbeat hb1 = new Heartbeat("some", 7);
        Heartbeat hb2 = new Heartbeat("other", 7);
        this.board.updateHeartbeats(asList(hb1, hb2));
        Collection<ExtendedHeartbeat> heartbeats = this.board.heartbeats();
        assertEquals(2, heartbeats.size());
        List<String> names = new LinkedList<>();
        for (Heartbeat hb : heartbeats) {
            names.add(hb.address());
        }
        assertTrue(names.contains("some"));
        assertTrue(names.contains("other"));
    }

    @Test
    public void updatesHeartbeatsCheckNotUpdated() {
        Heartbeat hb1 = new Heartbeat("some", 7);
        Heartbeat hb2 = new Heartbeat("other", 7);
        this.board.updateHeartbeats(asList(hb1, hb2));
        hb1.clock(2);
        this.board.updateHeartbeats(asList(hb1));
        hb1.clock(3);
        this.board.updateHeartbeats(asList(hb1));
        Collection<ExtendedHeartbeat> heartbeats = this.board.heartbeats();
        for (ExtendedHeartbeat hb : heartbeats) {
            switch (hb.address()) {
            case "some":
                assertEquals(0, hb.missingRounds());
                break;
            case "other":
                assertEquals(2, hb.missingRounds());
                break;
            }
        }
    }

    @Test
    public void updatesRemoveDeadNodes() {
        Heartbeat hb1 = new Heartbeat("some", 7);
        Heartbeat hb2 = new Heartbeat("other", 7);
        this.board.updateHeartbeats(asList(hb1, hb2));
        for (int i = 0; i <= MembershipBoard.MISSING_ROUNDS_TO_DEATH; i++) {
            hb1.clock(hb1.clock() + 1);
            this.board.updateHeartbeats(asList(hb1));
        }
        assertEquals(1, this.board.heartbeats().size());
    }
}
