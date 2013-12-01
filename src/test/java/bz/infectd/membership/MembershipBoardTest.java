package bz.infectd.membership;

import static bz.infectd.Configuration.getConfiguration;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.reset;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bz.infectd.event.EventBus;

public class MembershipBoardTest {
    private MembershipBoard board;
    private EventBus bus;

    @Before
    public void setUp() {
        this.bus = createMock(EventBus.class);
        this.board = new MembershipBoard(this.bus);
    }

    @After
    public void tearDown() {
        reset(this.bus);
    }

    @Test
    public void notifiesNodeJoinedEvent() {
        Heartbeat hb1 = new Heartbeat("some", 7);
        this.bus.nodeJoined("some", 7);
        replayAll();
        this.board.updateHeartbeats(asList(hb1));
        verifyAll();
    }

    @Test
    public void notifiesNodeLeftEvent() {
        Heartbeat hb1 = new Heartbeat("some", 7);
        Heartbeat hb2 = new Heartbeat("other", 7);
        this.bus.nodeJoined("some", 7);
        this.bus.nodeJoined("other", 7);
        this.bus.nodeLeft("other", 7);
        replayAll();
        this.board.updateHeartbeats(asList(hb1, hb2));
        for (int i = 0; i <= getConfiguration().roundsCount(); i++) {
            hb1.clock(hb1.clock() + 1);
            this.board.updateHeartbeats(asList(hb1));
        }
        verifyAll();
    }

    @Test
    public void updatesHeartbeats() {
        Heartbeat hb1 = new Heartbeat("some", 7);
        Heartbeat hb2 = new Heartbeat("other", 7);
        this.board.updateHeartbeats(asList(hb1, hb2));
        Collection<Heartbeat> heartbeats = this.board.heartbeats();
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
        Collection<Heartbeat> heartbeats = this.board.heartbeats();
        for (Heartbeat hb : heartbeats) {
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
    public void updatesHeartbeatsReturnModifieds() {
        Heartbeat hb1 = new Heartbeat("some", 7);
        Heartbeat hb2 = new Heartbeat("other", 7);
        Collection<Heartbeat> modified = this.board.updateHeartbeats(asList(hb1, hb2));
        assertEquals(2, modified.size());
        hb1.clock(2);
        modified = this.board.updateHeartbeats(asList(hb1, hb2));
        assertEquals(1, modified.size());
    }

    @Test
    public void updatesRemoveDeadNodes() {
        Heartbeat hb1 = new Heartbeat("some", 7);
        Heartbeat hb2 = new Heartbeat("other", 7);
        this.board.updateHeartbeats(asList(hb1, hb2));
        for (int i = 0; i <= getConfiguration().roundsCount(); i++) {
            hb1.clock(hb1.clock() + 1);
            this.board.updateHeartbeats(asList(hb1));
        }
        assertEquals(1, this.board.heartbeats().size());
    }
}
