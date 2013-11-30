package bz.infectd.journaling;

import java.util.Collection;
import java.util.HashSet;

import bz.infectd.membership.Heartbeat;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class EntriesCollection {

    private Collection<Heartbeat> heartbeats = new HashSet<>();

    public void add(Heartbeat heartbeat) {
        this.heartbeats.add(heartbeat);
    }

    public Collection<Heartbeat> heartbeats() {
        return this.heartbeats;
    }

    public int size() {
        return this.heartbeats.size();
    }
}
