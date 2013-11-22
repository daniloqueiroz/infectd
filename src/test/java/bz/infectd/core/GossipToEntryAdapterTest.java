package bz.infectd.core;

import static bz.infectd.communication.gossip.MessageFactory.createMessage;
import static org.easymock.EasyMock.eq;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Test;

import bz.infectd.communication.gossip.GossipHandler;
import bz.infectd.communication.gossip.protocol.P2PProtocol.Gossip;
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
        GossipHandler adapter = new GossipToEntryAdapter(journal);
        journal.add(eq(Entry.Builder.createEntry(hb)));
        replayAll();
        
        adapter.addEntry(msg);
        verifyAll();
    }

}
