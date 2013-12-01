package bz.infectd.event;

import java.util.Arrays;

import com.google.common.base.Objects;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Event {

    public static final String NODE_JOINED = "member-joined";
    public static final String NODE_LEFT = "member-left";
    private String name;
    private String[] args;

    /**
     * @param eventName
     *            The event name
     * @param args
     *            The event arguments
     */
    public Event(String eventName, String... args) {
        this.name = eventName;
        this.args = args;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name, this.args);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Event) {
            Event other = (Event) obj;
            return this.name.equals(other.name) && Arrays.equals(this.args, other.args);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("%s: %s", this.name, Arrays.toString(this.args));
    }
}
