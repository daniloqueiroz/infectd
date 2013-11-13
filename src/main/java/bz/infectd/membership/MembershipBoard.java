package bz.infectd.membership;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Keep track of all the current members by managing they heartbeats.
 * This class is responsible by mark missing member, remove members missing for
 * long and notify the system about members joins/leaves.
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class MembershipBoard {

    // TODO move to config
    public static final int MISSING_ROUNDS_TO_DEATH = 3;

    private Map<String, ExtendedHeartbeat> heartbeats = new HashMap<>();

    /**
     * Gets all the heartbeats.
     */
    public Collection<ExtendedHeartbeat> heartbeats() {
        return this.heartbeats.values();
    }

    /**
     * Updates the given heartbeats.
     */
    public void updateHeartbeats(Collection<Heartbeat> heartbeats) {
        for (Heartbeat heartbeat : heartbeats) {
            String address = heartbeat.address();
            int port = heartbeat.port();
            int clock = heartbeat.clock();
            this.updateHearbeat(address, port, clock);
        }
        Collection<String> toBeRemoved = this.sanitizeHeartbeats();
        this.removeDeads(toBeRemoved);
    }

    /**
     * Do the actual updates a given heartbeart.
     * 
     * When updating new heartbeats it fires a NewMemberEvent
     */
    private void updateHearbeat(String address, int port, int clock) {
        String key = createHeartbeatKey(address, port);
        if (this.heartbeats.containsKey(key)) {
            ExtendedHeartbeat hb = this.heartbeats.get(key);
            hb.clock(clock);
        } else {
            ExtendedHeartbeat hb = new ExtendedHeartbeat(address, port, clock);
            this.heartbeats.put(key, hb);
            // TODO notify new member joined
        }
    }

    /**
     * This method checks which heartbeats hadn't been updated since last
     * 'sanitization' and mark them as missing. The {@link ExtendedHeartbeat}
     * marked as missing for more than
     * {@link MembershipBoard#MISSING_ROUNDS_TO_DEATH} are returned by this
     * method.
     * 
     * @return The {@link String} keys for the heartbeats marked as missing for
     *         more than {@link MembershipBoard#MISSING_ROUNDS_TO_DEATH}
     */
    private Collection<String> sanitizeHeartbeats() {
        Collection<String> toBeRemove = new LinkedList<>();
        for (ExtendedHeartbeat heartbeat : this.heartbeats.values()) {
            if (heartbeat.hasChanged()) {
                heartbeat.markNotChanged();
            } else {
                heartbeat.markMissing();
                if (heartbeat.missingRounds() > MISSING_ROUNDS_TO_DEATH) {
                    toBeRemove.add(createHeartbeatKey(heartbeat.address(), heartbeat.port()));
                }
            }
        }
        return toBeRemove;
    }

    /**
     * Remove the given heartbeats from this board. This method also fires a
     * MemberLeftEvent.
     * 
     * @param toRemove
     *            the {@link String} keys for the heartbeats to be removed
     */
    private void removeDeads(Collection<String> toRemove) {
        for (String nodeKey : toRemove) {
            this.heartbeats.remove(nodeKey);
            // TODO notify member left
        }
    }

    private static String createHeartbeatKey(String address, int port) {
        return String.format("%s:%s", address, port);
    }
}
