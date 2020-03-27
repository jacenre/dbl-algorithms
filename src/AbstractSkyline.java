import java.util.ArrayList;

public abstract class AbstractSkyline {

    public abstract boolean testSpreadConstraint(Rectangle rectangle, SegPoint segPoint);

    public abstract int getLocalWaste(Rectangle rectangle, SegPoint position);

    public abstract int getFitnessNumber(Rectangle rectangle, SegPoint position);

    public abstract void addRectangle(Rectangle rectangle, SegPoint position);

    public abstract ArrayList<SegPoint> getCandidatePoints();

    public abstract int getMostLeftPoint();
}
