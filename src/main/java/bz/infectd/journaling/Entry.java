package bz.infectd.journaling;

import bz.infectd.membership.Heartbeat;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Entry<T> {
    private T content;

    protected Entry(T content) {
        this.content = content;
    }

    public T unwrap() {
        return this.content;
    }

    @Override
    public int hashCode() {
        return this.unwrap().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Entry) {
            return this.unwrap().equals(((Entry<?>) obj).unwrap());
        } else {
            return false;
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.unwrap().toString();
    }

    /**
     * Simple builder for entries
     */
    public static class Builder {
        public static Entry<Heartbeat> createEntry(Heartbeat hb) {
            return new Entry<Heartbeat>(hb);
        }
    }
}
