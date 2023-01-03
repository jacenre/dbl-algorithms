package jacenre.dbla;
public class PositionRectangleRotationPair {
    Rectangle rectangle;
    SegPoint position;
    boolean rotated;

    PositionRectangleRotationPair(Rectangle rec, SegPoint pnt, boolean rotated) {
        this.rectangle = rec;
        this.position = pnt;
        this.rotated = rotated;
    }
}