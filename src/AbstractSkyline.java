import java.util.ArrayList;

/**
 * The idea of this ADT is that it maintains the skyline, but does not concern itself with placing the actual
 * rectangles. It should therefore not place any rectangles, but only keep track of the skyline.
 */
public abstract class AbstractSkyline {

    /**
     * Checks if adding a certain rectangle to a certain candidate position will be rejected because the spread constraint
     * will not hold.
     * @param rectangle The rectangle to test for placement
     * @param position The position where the Rectangle could be placed
     * @returns (position.x + rectangle.width - getMostLeftPoint() > spread constraint)
     */
    public abstract boolean testSpreadConstraint(Rectangle rectangle, SegPoint position);

    /**
     * Computes how much space would be wasted if a rectangle would be placed on a certain position
     * @param rectangle The rectangle to be placed
     * @param position The position where the rectangle should be placed
     * @param sequence The other rectangles that have not been placed, which is needed to compute the minimum height
     *                 and minimum width of these rectangles.
     * @returns a number which tells how much local waste there would be if this rectangle were to be placed
     */
    public abstract int getLocalWaste(Rectangle rectangle, SegPoint position, ArrayList<Rectangle> sequence);

    /**
     * If two potential placements give the same minimum local waste, then this function might determine the
     * rectangle which should be placed. It find the fitness number of a rectangles, indicing how "perfect" the fit is.
     * @param rectangle The rectangle to be placed
     * @param position The position where the rectangle would be placed
     * @returns an integer in the set of { 0, 1, 2, 3, 4 }
     */
    public abstract int getFitnessNumber(Rectangle rectangle, SegPoint position);

    /**
     * Adjusts the skyline if a rectangle were to be placed at a certain position.
     * @param rectangle The rectangle to be placed
     * @param position The position where the rectangle would be placed
     */
    public abstract void adjustSkyline(Rectangle rectangle, SegPoint position);

    /**
     * Checks if there are any segments in the skyline for which there is only one rectangle that fits perfectly, and
     * there are no other rectangles smaller than this specific rectangle. If found, returns both the position and
     * rectangle in a Record called PositionRectanglePair.
     * @param sequence The ordered set of rectangles that have not been placed yet
     * @returns a position-rectangle pair if it was found
     */
    public abstract PositionRectanglePair anyOnlyFit(ArrayList<Rectangle> sequence);

    /**
     * Looks at current skyline and determines the candidate points where a new rectangle could be placed, puts it in
     * a linked list and returns it.
     * @returns A list of candidate points (which are on a segment). The class SegPoint keeps track of the relative
     * position of the candidate point on the segment (either at the start (or upper point of the segment) or the end
     * (lower point))
     */
    public abstract ArrayList<SegPoint> getCandidatePoints();

    /**
     * Simple method to get the most left segment in the skyline, which is needed for the maximum spread constraint
     * @returns the most left x position of the segments.
     */
    public abstract int getMostLeftPoint();
}
