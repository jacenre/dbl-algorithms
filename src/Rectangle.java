import java.util.UUID;

/**
 * Custom version of {@link java.awt.Rectangle} that allows for extra satellite data needed for the algorithm.
 */
public class Rectangle extends java.awt.Rectangle {

    /**
     * {@code true} if rectangle is rotated.
     */
    private boolean rotated = false;

    /**
     * Unique id used for describing the order of the input. Needed to restore order when outputting.
     */
    private String id;

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

        // Assign ID
        this.id = UUID.randomUUID().toString();
    }

    public Rectangle(Rectangle rectangle) {
        super(rectangle);

        // Assign ID
        this.id = rectangle.id != null ? rectangle.id : UUID.randomUUID().toString();
    }

    public Rectangle(int x, int y, int width, int height) {
        super(x, y, width, height);

        // Assign ID
        this.id = UUID.randomUUID().toString();
    }

    /** Get rotated property. */
    public boolean isRotated() {
        return rotated;
    }

    /** Get id. */
    public String getId() {
        return id;
    }

    /**
     * Command to rotate the rectangle.
     * Allowed to be used in the strategy variants where rotation of the rectangle is used.
     */
    public void rotate() {
        //noinspection SuspiciousNameCombination
        this.setBounds(this.x, this.y, this.height, this.width);
        this.rotated = !this.rotated;
    }
}
