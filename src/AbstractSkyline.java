import java.util.ArrayList;

public abstract class AbstractSkyline {

    /**
     * Checks if adding a certain rectangle to a certain candidate position will be rejected because the spread constraint
     * will not hold.
     * @param rectangle The rectangle to test for placement
     * @param position The position where the Rectangle could be placed
     * @returns (position.x + rectangle.width - getMostLeftPoint() > spread constraint)
     */
    public abstract boolean testSpreadConstraint(Rectangle rectangle, SegPoint position);

    public abstract int getLocalWaste(Rectangle rectangle, SegPoint position, ArrayList<Rectangle> sequence);

    public abstract int getFitnessNumber(Rectangle rectangle, SegPoint position);

    public abstract void addRectangle(Rectangle rectangle, SegPoint position);

    public abstract PositionRectanglePair anyOnlyFit(ArrayList<Rectangle> sequence);

    public abstract ArrayList<SegPoint> getCandidatePoints();


    public abstract int getMostLeftPoint();
}
