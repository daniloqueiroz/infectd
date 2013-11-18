package bz.infectd.journaling;

import java.util.Collection;
import java.util.LinkedList;

import bz.infectd.membership.Heartbeat;
import bz.infectd.membership.MembershipBoard;

/**
 * Kill this class?!
 * 
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class EntriesProcessor {

    private MembershipBoard membersBoard;

    public EntriesProcessor(MembershipBoard board) {
        this.membersBoard = board;
    }

    public void process(Collection<Entry<?>> entries) {
        ContentsManager contents = new ContentsManager();
        for (Entry<?> entry : entries) {
            contents.add(entry);
        }

        this.membersBoard.updateHeartbeats(contents.heartbeats);
    }

    private class ContentsManager {
        final Collection<Heartbeat> heartbeats = new LinkedList<>();

        public void add(Entry<?> entry) {
            Object content = entry.unwrap();
            if (content instanceof Heartbeat) {
                this.heartbeats.add((Heartbeat) content);
            }
        }
    }
}
