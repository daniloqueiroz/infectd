package bz.infectd.journaling;

/**
 * @author Danilo Queiroz <dpenna.queiroz@gmail.com>
 */
public class Entry<T> {
    private T content;

    public Entry(T content) {
        this.content = content;
    }

    public T unwrap() {
        return this.content;
    }
}
