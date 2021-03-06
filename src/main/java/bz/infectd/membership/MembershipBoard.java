package bz.infectd.membership;

import static bz.infectd.Configuration.getConfiguration;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

/**
 * Keep track of all the current members by managing they heartbeats. This class
 * is responsible by mark missing member, remove members missing for long and
 * notify the system about members joins/leaves.
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class MembershipBoard {

    private static final Logger LOG = getLogger(MembershipBoard.class);
    private Map<String, Heartbeat> heartbeats = new HashMap<>();

    /**
     * Gets all the heartbeats.
     */
    public List<Heartbeat> heartbeats() {
        return new ArrayList<>(this.heartbeats.values());
    }

    /**
     * Updates the given heartbeats.
     * 
     * @return The heartbeats that triggered modifications
     */
    public Collection<Heartbeat> updateHeartbeats(Collection<Heartbeat> heartbeats) {
        LOG.info("Updating {} heartbeats", heartbeats.size());
        Collection<Heartbeat> modified = new LinkedList<>();
        for (Heartbeat heartbeat : heartbeats) {
            if (this.updateHearbeat(heartbeat)) {
                modified.add(heartbeat);
            }
        }
        Collection<String> toBeRemoved = this.sanitizeHeartbeats();
        this.removeDeads(toBeRemoved);
        return modified;
    }

    /**
     * Do the actual updates a given heartbeart.
     * 
     * When updating new heartbeats it fires a NewMemberEvent
     */
    private boolean updateHearbeat(Heartbeat heartbeat) {
        boolean modified = true;
        String key = createHeartbeatKey(heartbeat.address(), heartbeat.port());
        if (this.heartbeats.containsKey(key)) {
            Heartbeat originalHeartbeat = this.heartbeats.get(key);
            originalHeartbeat.clock(heartbeat.clock());
            modified = originalHeartbeat.hasChanged();
        } else {
            this.heartbeats.put(key, heartbeat);
            // TODO notify new member joined
            LOG.info("Node {} has joined - adding heartbeat", key);
        }
        return modified;
    }

    /**
     * This method checks which heartbeats hadn't been updated since last
     * 'sanitization' and mark them as missing. The {@link Heartbeat} marked as
     * missing for more than {@link MembershipBoard#MISSING_ROUNDS_TO_DEATH} are
     * returned by this method.
     * 
     * @return The {@link String} keys for the heartbeats marked as missing for
     *         more than {@link MembershipBoard#MISSING_ROUNDS_TO_DEATH}
     */
    private Collection<String> sanitizeHeartbeats() {
        Collection<String> toBeRemove = new LinkedList<>();
        for (Heartbeat heartbeat : this.heartbeats.values()) {
            if (heartbeat.hasChanged()) {
                heartbeat.markNotChanged();
            } else {
                heartbeat.markMissing();
                if (heartbeat.missingRounds() > getConfiguration().roundsCount()) {
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
            LOG.info("Node {} dead - removing heartbeat", nodeKey);
        }
    }

    private static String createHeartbeatKey(String address, int port) {
        return String.format("%s:%s", address, port);
    }
}
