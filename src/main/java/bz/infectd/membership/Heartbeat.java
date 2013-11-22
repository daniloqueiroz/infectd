package bz.infectd.membership;
import static java.lang.Math.max;

import com.google.common.base.Objects;

/**
 * Holds the heartbeat clock for a given node. It uses a Lamport Clock to track
 * the heartbeat events.
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Heartbeat {

    private int clock = 1;
    private String address;
    private int port;

    public Heartbeat(String address, int port) {
        this.address = address;
        this.port = port;
    }

    /**
     * It adjust the clock for the given clock only, and if only, the given
     * clock value is bigger than the current value for the clock.
     * 
     * @param clock
     *            The new clock's value.
     */
    public void clock(int clock) {
        this.clock = max(this.clock(), clock);
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
    
    /* (non-Javadoc)
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
}