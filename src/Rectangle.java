import java.util.UUID;

/**
 * Custom version of {@link java.awt.Rectangle} that allows for extra satalite data needed for the algorithm.
 */
public class Rectangle extends java.awt.Rectangle {

    /**
     * {@code true} if rectangle is rotated.
     */
    public boolean rotated = false;

    /**
     * Unique id used for describing the order of the input. Needed to restore order when outputting.
     */
    public String id;

    /**
     * Construction method which also assigns a random unique id.
     *
     * @param width The width of the {@code Rectangle}.
     * @param height The height of the {@code Rectangle}.
     *
     * @see java.awt.Rectangle
     */
    public Rectangle(int width, int height) {
        super(width, height);
        this.id = UUID.randomUUID().toString();
    }
}
