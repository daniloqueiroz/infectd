package bz.infectd.membership;

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
public class ExternalHeartbeat extends Heartbeat {

    int missingRounds;
    boolean changed;

    public ExternalHeartbeat(int clock) {
        super.clock(clock);
        this.changed = true;
    }

    @Override
    public void clock(int clock) {
        if (clock > this.clock()) {
            super.clock(clock);
            this.missingRounds = 0;
            this.changed = true;
        }
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
}