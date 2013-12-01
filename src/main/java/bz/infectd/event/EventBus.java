package bz.infectd.event;

import static bz.infectd.event.Event.NODE_JOINED;
import static bz.infectd.event.Event.NODE_LEFT;
import static java.lang.String.valueOf;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class EventBus {

    private com.google.common.eventbus.EventBus bus;

    /**
     * For tests purpose
     */
    protected EventBus(com.google.common.eventbus.EventBus eventBus) {
        this.bus = eventBus;
    }

    public EventBus() {
        this(new com.google.common.eventbus.EventBus());
    }

    public void nodeJoined(String address, int port) {
        this.bus.post(new Event(NODE_JOINED, address, valueOf(port)));
    }

    /**
     * @param string
     * @param i
     */
    public void nodeLeft(String address, int port) {
        this.bus.post(new Event(NODE_LEFT, address, valueOf(port)));
    }

}
