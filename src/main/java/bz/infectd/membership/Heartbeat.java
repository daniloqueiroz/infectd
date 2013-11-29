package bz.infectd.membership;

import static java.lang.String.format;
import bz.infectd.communication.gossip.Propagable;

import com.google.common.base.Objects;


/**
 * Encapsulates all the extra-knowledge necessary to handle heartbeats from
 * external nodes. It extends the {@link Heartbeat} class by adding tracking of
 * for how many rounds the heartbeat hasn't being updated.
 * 
 * This heartbeats are marked as changed every time the clock changes. As
 * heartbeats has no notion of rounds, it's necessary to manually mark then as
 * missing every round.
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Heartbeat implements Propagable {

    int missingRounds;
    boolean changed;
    private int clock = 1;
    private String address;
    private int port;

    public Heartbeat(String address, int port) {
        this(address, port, 0);
    }
    
    public Heartbeat(String address, int port, int clock) {
        this.address = address;
        this.port = port;
        this.clock(clock);
        this.changed = true;
    }

    public void clock(int newClock) {
        if (newClock > this.clock()) {
            this.clock = newClock;
            this.missingRounds = 0;
            this.changed = true;
        }
    }

    /**
     * Getter for the clock.
     * 
     * @return the current value of the heartbeat clock.
     */
    public int clock() {
        return this.clock;
    }

    public String address() {
        return this.address;
    }

    public int port() {
        return this.port;
    }

    /**
     * Getter for the MissingRounds.
     * 
     * @return the number of rounds since the last update of this heartbeat.
     */
    public int missingRounds() {
        return this.missingRounds;
    }

    /**
     * Marks this heartbeat as missing for one round.
     */
    public void markMissing() {
        this.missingRounds++;
    }

    /**
     * Checks if this {@link Heartbeat} has changed
     * 
     * @return *true* if this heartbeat has being update since the last round,
     *         *false* otherwise.
     */
    public boolean hasChanged() {
        return this.changed;
    }

    /**
     * Force to mark this heartbeat as not changed.
     */
    public void markNotChanged() {
        this.changed = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(this.address(), this.port(), this.clock());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Heartbeat) {
            Heartbeat other = (Heartbeat) obj;
            return (this.port() == other.port() && this.address().equals(other.address()) && this
                    .clock() == other.clock());
        } else {
            return false;
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return format("peer: %s:%s - clock: %s", this.address(), this.port(), this.clock());
    }
}