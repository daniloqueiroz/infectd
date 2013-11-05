package bz.infectd.membership;

import static java.lang.Math.max;

/**
 * Holds the heartbeat clock for a given node.
 * It uses a Lamport Clock to track the heartbeat events.
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Heartbeat {

    private int clock = 1;

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

}