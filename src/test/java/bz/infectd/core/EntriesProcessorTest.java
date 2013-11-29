package bz.infectd.core;

import static bz.infectd.journaling.Entry.Builder.createEntry;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import bz.infectd.journaling.Entry;
import bz.infectd.membership.Heartbeat;
import bz.infectd.membership.MembershipBoard;

public class EntriesProcessorTest {

    @Test
    public void sendsHeartbeartsToMembership() {
        MembershipBoard board = createMock(MembershipBoard.class);
        EntriesProcessor adapter = new EntriesProcessor(board);
        Collection<Heartbeat> heartbeats = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            heartbeats.add(createMock(Heartbeat.class));
        }
        Collection<Entry<?>> entries = new LinkedList<>();
        for (Heartbeat hb : heartbeats) {
            entries.add(createEntry(hb));
        }
        expect(board.updateHeartbeats(heartbeats)).andReturn(heartbeats);
        expect(board.heartbeats()).andReturn(new LinkedList<Heartbeat>());
        replayAll();
        adapter.process(entries);
        verifyAll();
    }
}
