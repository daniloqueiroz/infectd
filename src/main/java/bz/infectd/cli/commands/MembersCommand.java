package bz.infectd.cli.commands;

import java.util.Collection;

import bz.infectd.membership.Heartbeat;
import bz.infectd.membership.MembershipBoard;

import com.google.inject.Inject;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class MembersCommand implements InfectdCommand {

    private MembershipBoard board;

    @Inject
    public MembersCommand(MembershipBoard board) {
        this.board = board;
    }

    @Override
    public String execute(String... params) throws InfectdCommandException {
        String response = "No other members online";
        Collection<Heartbeat> hearbeats = this.board.heartbeats();
        if (!hearbeats.isEmpty()) {
            StringBuilder buf = new StringBuilder();
            for (Heartbeat hb : hearbeats) {
                buf.append(hb.toString());
                buf.append('\n');
            }
            response = buf.toString();
        }
        return response;
    }

}
