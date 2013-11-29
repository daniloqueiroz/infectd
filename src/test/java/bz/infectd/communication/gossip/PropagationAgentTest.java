package bz.infectd.communication.gossip;

import static bz.infectd.Configuration.getConfiguration;
import static bz.infectd.communication.gossip.MessageFactory.createMessage;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.createPartialMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import bz.infectd.Configuration;
import bz.infectd.communication.gossip.udp.Client;
import bz.infectd.membership.Heartbeat;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PropagationAgent.class)
public class PropagationAgentTest {

    private Client agentMock;
    private Configuration config;
    private int port;

    @Before
    public void setUp() {
        this.agentMock = createMock(Client.class);
        this.config = getConfiguration();
        this.port = 7770;
    }

    @Test
    public void propagatesSelectsAndInfects() throws Exception {
        PropagationAgent<?> agent = createPartialMock(PropagationAgent.class, new String[] { "infect",
                "selectMembers" }, null, null);
        List<Heartbeat> selected = new ArrayList<>();
        expect(agent.selectMembers()).andReturn(selected);
        agent.infect(selected);
        replayAll();
        agent.propagate();
        verifyAll();
    }

    @Test
    public void infectsOneEntryToOneMember() throws Exception {
        Heartbeat entryHb = new Heartbeat("192.168.1.2", this.port);
        Collection<Heartbeat> entries = new LinkedList<>();
        entries.add(entryHb);
        PropagationAgent<Heartbeat> agent = new PropagationAgent<>(entries, null);
        expectNew(Client.class, "127.0.0.1", this.port).andReturn(this.agentMock);
        this.agentMock.send(eq(createMessage(entryHb)));
        replayAll();
        List<Heartbeat> beats = Arrays.asList(new Heartbeat("127.0.0.1", this.port,
                1));
        agent.infect(beats);
        verifyAll();
    }

    @Test
    public void selectsAllMembers() throws Exception {
        List<Heartbeat> beats = new LinkedList<>();
        for (int i = 0; i < this.config.minimunPropagationFactor(); i++) {
            beats.add(new Heartbeat("127.0.0.1", ++this.port, 1));
        }
        PropagationAgent<?> agent = new PropagationAgent<>(null, beats);
        Collection<Heartbeat> selected = agent.selectMembers();
        assertEquals(this.config.minimunPropagationFactor(), selected.size());
    }

    @Test
    public void selectsMinimumAmountOfMembers() throws Exception {
        List<Heartbeat> beats = new LinkedList<>();
        int port = 7770;
        for (int i = 0; i <= this.config.minimunPropagationFactor(); i++) {
            beats.add(new Heartbeat("127.0.0.1", ++port, 1));
        }
        PropagationAgent<?> agent = new PropagationAgent<>(null, beats);
        assertEquals(this.config.minimunPropagationFactor(), agent.selectMembers().size());
    }

    @Test
    public void selectsAmountOfMembersFromPropagationFactor() throws Exception {
        List<Heartbeat> beats = new LinkedList<>();
        int port = 7770;
        for (int i = 0; i < 100; i++) {
            beats.add(new Heartbeat("127.0.0.1", ++port, 1));
        }
        int expectedNumberOfElements = (int) (beats.size() * this.config.propagationFactor());
        PropagationAgent<?> agent = new PropagationAgent<>(null, beats);
        assertEquals(expectedNumberOfElements, agent.selectMembers().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void selectsRandomElements() throws Exception {
        ArrayList<Heartbeat> beats = new ArrayList<>();
        int port = 7770;
        for (int i = 0; i < 100; i++) {
            beats.add(new Heartbeat("127.0.0.1", ++port, 0));
        }
        Collection<? extends Heartbeat> elements1 = new PropagationAgent<>(null,
                (List<Heartbeat>) beats.clone()).selectMembers();
        Collection<? extends Heartbeat> elements2 = new PropagationAgent<>(null,
                (List<Heartbeat>) beats.clone()).selectMembers();
        assertNotEquals(elements1, elements2);
        assertEquals(elements1.size(), elements2.size());
    }
}
