package bz.infectd.journaling;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import bz.infectd.communication.gossip.PropagationAgent;
import bz.infectd.membership.Heartbeat;
import bz.infectd.membership.MembershipBoard;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Journal.class)
public class JournalTest {

    private Journal journal;
    private MembershipBoard board;

    @Before
    public void setUp() {
        this.board = createMock(MembershipBoard.class);
        this.journal = new Journal(this.board);
    }

    @Test
    public void addsAndGetEntries() {
        Heartbeat hb1 = createMock(Heartbeat.class);
        Heartbeat hb2 = createMock(Heartbeat.class);
        this.journal.add(hb1);
        this.journal.add(hb2);
        int size = this.journal.entries.size();
        assertEquals(2, size);
    }

    @Test
    public void addsNoDuplicates() {
        Heartbeat hb = createMock(Heartbeat.class);
        this.journal.add(hb);
        this.journal.add(hb);
        assertEquals(1, this.journal.entries.size());
    }

    @Test
    public void syncs() throws Exception {
        for (int i = 0; i < 5; i++) {
            this.journal.add(createMock(Heartbeat.class));
        }
        Collection<Heartbeat> heartbeats = this.journal.entries.heartbeats();
        List<Heartbeat> hbList = new ArrayList<>(heartbeats);
        expect(this.board.updateHeartbeats(heartbeats)).andReturn(heartbeats);
        expect(this.board.heartbeats()).andReturn(hbList);
        PropagationAgent<?> agent = createMock(PropagationAgent.class);
        expectNew(PropagationAgent.class, heartbeats, hbList).andReturn(agent);
        agent.propagate();
        replayAll();
        this.journal.sync();
        verifyAll();
        assertEquals(0, this.journal.entries.size());
    }
}
