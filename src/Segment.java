import java.awt.*;

/**
 * Util class representing a line segment.
 * <p>
 * Has overlap with {@link Util.Segment} but differs in functionality.
 * // TODO merge this and Util.Segment
 * </p>
 */
class Segment {

    SegPoint start = null;
    SegPoint end = null;

    Segment(SegPoint start, SegPoint end) {
        this.start = start;
        this.end = end;
    }

    int getLength() {
        return end.y - start.y;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Segment) {
            // For segments we check if the points are the same.
            return this.start == ((Segment) obj).start && this.end == ((Segment) obj).end;
        } else if (obj instanceof Rectangle) {
            // For rectangles we check if the
            return (this.start.equals(new Point(((Rectangle) obj).x, ((Rectangle) obj).y))
                    || this.end.equals(new Point(((Rectangle) obj).x,((Rectangle) obj).x + ((Rectangle) obj).height)));
        }
        return super.equals(obj);
    }
}