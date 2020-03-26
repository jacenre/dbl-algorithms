import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Solver algorithm using the Skyline heuristic
 *
 * @see <a href="https://www-sciencedirect-com.dianus.libr.tue.nl/science/article/pii/S0377221711005510">source</a>
 */
public class SkylineSolver extends AbstractSolver {

    int SPREAD_CONSTRAINT = 100;
//    // ArrayList representing the skyline, starting of with a segment corresponding to the height of the sheet.
//    ArrayList<Segment> skyline = new ArrayList<>(); // Important to keep this ordered
//        skyline.add(new Segment(new Point(0, 0), new Point(0, parameters.height)));

    // Algorithm 2 in the paper
    @Override
    Solution pack(Parameters parameters) {
        int lowerBound = getLowerBound(parameters);
        int upperBound = (int) (lowerBound * 1.1);
        int iter = 1;
        boolean upperBoundFound = false;

        while(/*time limit not reached and */ lowerBound != upperBound) {
            int tempLowerBound = lowerBound;
            while (tempLowerBound < upperBound) {
                int width = (int)((tempLowerBound + upperBound) / 2);
                if (solve(width, iter)) {
                    /* record this solution */
                    upperBound = width;
                    upperBoundFound = true;
                } else {
                    tempLowerBound = width + 1;
                }
            }
            if (upperBoundFound == false)
                upperBound = (int)(upperBound * 1.1);
            iter *= 2;
        }
        return new Solution(parameters);
    }

    int getLowerBound(Parameters parameters) {
        int totalArea = 0;
        int LB2 = 0;
        double LB3 = 0;
        for (Rectangle rec : parameters.rectangles) {
            totalArea += rec.getHeight() * rec.getWidth();
            LB2 += rec.getWidth();
            if (rec.height == parameters.height / 2) {
                LB3 += rec.width;
            };
        }
        int LB1 = (int)Math.ceil(totalArea/(double)parameters.height);

        if (parameters.rotationVariant) {
            return LB1;
        }

        return Math.max(LB1, LB2 + (int)Math.ceil(LB3 / 2));
    }

    // Algorithm 1 in the paper
    boolean solve(int W, int iter) {

    }

    ArrayList<Point> getCandidatePositions(ArrayList<Segment> skyline) {
        ArrayList<Point> points = new ArrayList<>();
        points.add(skyline.get(0).start);
        points.add(skyline.get(skyline.size() - 1).end);

        if (skyline.size() == 1) {
            return points;
        }

        // At least two segments

        // Check edge cases
        if (skyline.get(0).end.x < skyline.get(1).start.x) {
            points.add(skyline.get(0).end);
        }
        if (skyline.get(skyline.size() - 1).start.x < skyline.get(skyline.size() - 2).end.x) {
            points.add(skyline.get(skyline.size() - 1).start);
        }

        // Check all the middle ones
        for (int i = 1; i < skyline.size() - 1; i++) {

        }

        return points;
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

        int getLength() {
            return start.y - end.y;
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
            if (segment.hasLeftCandidate()) {

            } else if (segment.hasRightCandidate()) {

            }



            if (segment.equals(rectangle)) {
                // Check if rectangle is larger than the segment.
                if (rectangle.height > segment.start.distance(segment.end)) {

                }
            }
        }
    }

    int getMostLeftPoint(ArrayList<Segment> skyline) {
        int mostLeftPoint = Integer.MAX_VALUE;
        for (Segment segment : skyline) {
            if (segment.start.x < mostLeftPoint) {
                mostLeftPoint = segment.start.x;
            }
        }
        return mostLeftPoint;
    }
}
