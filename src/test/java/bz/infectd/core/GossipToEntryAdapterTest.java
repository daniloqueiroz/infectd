package bz.infectd.core;

import static bz.infectd.communication.protocol.MessageFactory.createMessage;
import static org.easymock.EasyMock.eq;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Test;

import bz.infectd.communication.protocol.gossip.P2PProtocol.Gossip;
import bz.infectd.core.GossipToEntryAdapter;
import bz.infectd.journaling.Entry;
import bz.infectd.journaling.Journal;
import bz.infectd.membership.Heartbeat;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 *
 */
public class GossipToEntryAdapterTest {

    @Test
    public void addsHeartbeatEntry() {
        Heartbeat hb = new Heartbeat("some", 1);
        hb.clock(5);
        Gossip msg = createMessage(hb);
        Journal journal = createMock(Journal.class);
        GossipToEntryAdapter adapter = new GossipToEntryAdapter(journal);
        journal.add(eq(Entry.Builder.createEntry(hb)));
        replayAll();
        
        adapter.addEntry(msg);
        verifyAll();
    }

}
