package bz.infectd.membership;

/**
 * This class is responsible by update the Monitored {@link Heartbeat} every
 * time a 'pulse' happens.
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class HeartbeatMonitor {

    private Heartbeat heartbeat;

    public HeartbeatMonitor(Heartbeat hb) {
        this.heartbeat = hb;
    }

    /**
     * Send a pulse to the heartbeat (increases the heartbeat clock)
     */
    public void pulse() {
        this.heartbeat.clock(this.heartbeat.clock() + 1);
    }
}
