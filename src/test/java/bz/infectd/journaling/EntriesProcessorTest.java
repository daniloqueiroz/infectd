package bz.infectd.journaling;

import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import bz.infectd.core.EntriesProcessor;
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
            entries.add(new Entry<Heartbeat>(hb));
        }
        board.updateHeartbeats(heartbeats);
        replayAll();
        adapter.process(entries);
        verifyAll();
    }
}
