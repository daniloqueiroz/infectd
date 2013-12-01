package bz.infectd.event;

import static org.easymock.EasyMock.eq;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Test;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class EventbusTest {

    @Test
    public void firesNodeJoined() {
        com.google.common.eventbus.EventBus guavaBus = createMock(com.google.common.eventbus.EventBus.class);
        EventBus bus = new EventBus(guavaBus);
        guavaBus.post(eq(new Event(Event.NODE_JOINED, "127.0.0.1", String.valueOf(7777))));
        replayAll();
        bus.nodeJoined("127.0.0.1", 7777);
        verifyAll();
    }

    @Test
    public void firesNodeLeft() {
        com.google.common.eventbus.EventBus guavaBus = createMock(com.google.common.eventbus.EventBus.class);
        EventBus bus = new EventBus(guavaBus);
        guavaBus.post(eq(new Event(Event.NODE_LEFT, "127.0.0.1", String.valueOf(7777))));
        replayAll();
        bus.nodeLeft("127.0.0.1", 7777);
        verifyAll();
    }
}
