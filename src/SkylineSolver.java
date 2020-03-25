import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Solver algorithm using the Skyline heuristic
 *
 * @see <a href="https://www-sciencedirect-com.dianus.libr.tue.nl/science/article/pii/S0377221711005510">source</a>
 */
public class SkylineSolver extends AbstractSolver {

    @Override
    Solution pack(Parameters parameters) {
        // ArrayList representing the skyline, starting of with a segment corresponding to the height of the sheet.
        ArrayList<Segment> skyline = new ArrayList<>();
        skyline.add(new Segment(new Point(0, 0), new Point(0, parameters.height)));

        for (Rectangle rectangle : parameters.rectangles) {

        }

        return new Solution(parameters);
    }

    /**
     * Util class representing a line segment.
     * <p>
     * Has overlap with {@link Util.Segment} but differs in functionality.
     * // TODO merge this and Util.Segment
     * </p>
     */
    class Segment {

        Point start = null;
        Point end = null;

        Segment(Point start, Point end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Segment) {
                // For segments we check if the points are the same.
                return this.start == ((Segment) obj).start && this.end == ((Segment) obj).end;
            } else if (obj instanceof Rectangle) {
                // For rectangles we check if the
                return this.start.equals(new Point(((Rectangle) obj).x, ((Rectangle) obj).y));
            }
            return super.equals(obj);
        }
    }


    /**
     * Places a rectangle and modifies the skyline accordingly.
     *
     * @param skyline   the Skyline to modify
     * @param rectangle the Rectangle to place
     */
    void place(ArrayList<Segment> skyline, Rectangle rectangle) {
        // Note that a rectangle is always placed with the origin matching a skyline point.
        for (Segment segment : skyline) {
            if (segment.equals(rectangle)) {
                // Check if rectangle is larger than the segment.
                if (rectangle.height > segment.start.distance(segment.end)) {

                }
            }
        }
    }

}
