package jacenre.dbla;
import java.awt.*;

/**
 * A class which is necessary to keep track of the relative position of the points to the segment of which they are part
 */
class SegPoint extends java.awt.Point {
    boolean start;

    public SegPoint (boolean start) {
        super();
        this.start = start;
    }

    public SegPoint (boolean start, int x, int y) {
        super(x, y);
        this.start = start;
    }

    public SegPoint (boolean start, Point p) {
        super(p);
        this.start = start;
    }
}