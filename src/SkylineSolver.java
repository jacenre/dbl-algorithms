import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Solver algorithm using the Skyline heuristic
 *
 * @see <a href="https://www-sciencedirect-com.dianus.libr.tue.nl/science/article/pii/S0377221711005510">source</a>
 */
public class SkylineSolver extends AbstractSolver {

    Solution globalSolution;

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
                int width = ((tempLowerBound + upperBound) / 2);
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
        return globalSolution;
    }

    /**
     * Gives the lowerbound for the binary search for a suitable width of the square area that is used in the second
     * algorithm.
     *
     * @param parameters
     * @return The lowerbound, which is dependent on rotations variant and the given rectangles
     */
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

    /**
     * Handles a Tabu search for a good sequence of rectangles and good maximum spread.
     *
     * @param W The width that has been supplied by the pack method
     * @param iter The iterations variant that has also been supplied by the pack method
     * @returns a boolean signalling if a solution could be found with the given attributes
     */
    boolean solve(int W, int iter) {

    }

    /**
     * Goes through the heurstics and places the sequence of rectangles in the box while maintaining a skyline view of
     * the whole ordeal. Returns if a solution was able to be found with the given maximumSpread and width.
     *
     * @param sequence The sequence of rectangles, which can be very different according to different sorting and
     *                 the random permutations made by the tabu search algorithm
     * @param skyline The skyline on which to work
     * @param width The given width to which to adhere
     * @param maximumSpread The given maximumSpread to which to adhere
     * @return true or false whether the heuristic was able to pack all the rectangles given the restrictions
     */
    boolean heuristicPacking(ArrayList<Rectangle> sequence, ArrayList<Segment> skyline, int width, int maximumSpread) {
        for (Segment segment : skyline) {
            for (Rectangle rectangle : sequence) {

            }
        }
    }

    /**
     * A class which is necessary to keep track of the relative position of the points to the segment of which they are part
     */
    class SegPoint extends java.awt.Point {
        boolean start;

        public SegPoint (boolean start) {
            super();
            this.start = start;
        }
    }

    /**
     * Computes all the candidate positions (where a new rectangle could be placed), given a skyline and width
     *
     * @param skyline The skyline to be used
     * @param width A width to which the skyline needs to be adhered to
     * @returns the list of points on which new rectangles can be placed
     */
    ArrayList<SegPoint> getCandidatePositions(ArrayList<Segment> skyline, int width) {
        ArrayList<SegPoint> points = new ArrayList<>();
        if (skyline.get(0).start.x < width) {
            points.add(skyline.get(0).start);
        }

        // Check all the middle ones
        for (int i = 0; i < skyline.size(); i++) {
            if (i != 0 && skyline.get(i).start.x < skyline.get(i-1).end.x) {
                points.add(skyline.get(i).start);
            }
            if (i != skyline.size() - 1 && skyline.get(i).end.x < skyline.get(i+1).start.x) {
                points.add(skyline.get(i).end);
            }
        }

        if (skyline.get(skyline.size() - 1).end.x < width) {
            points.add(skyline.get(skyline.size() - 1).end);
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

        SegPoint start = null;
        SegPoint end = null;

        Segment(SegPoint start, SegPoint end) {
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

    /**
     * Returns the most left segment in the skyline. Usefull for checking if a newly placed rectangles breaks the
     * spread constraint heuristic.
     *
     * @param skyline The skyline to be used
     * @returns An integer of the x position of the most left segment
     */
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
