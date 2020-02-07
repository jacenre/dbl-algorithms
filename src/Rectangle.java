import java.util.UUID;

public class Rectangle extends java.awt.Rectangle {
    public boolean rotated = false;
    public String id;
    public Rectangle(int width, int height) {
        super(width, height);
        this.id = UUID.randomUUID().toString();
    }
}
