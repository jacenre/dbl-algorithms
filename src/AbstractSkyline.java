import java.util.ArrayList;

public abstract class AbstractSkyline {

    public abstract boolean testSpreadConstraint(Rectangle rectangle);

    public abstract  int getFitnessNumber(Rectangle rectangle);

    public abstract boolean onlyFit(Rectangle rectangle);

    public abstract int getLocalWaste(Rectangle rectangle);

    public abstract void addRectangle(Rectangle rectangle);

    public abstract void removeRectangle(Rectangle rectangle);

    public abstract ArrayList<SegPoint> getCandidatePoints();

    public abstract int getMostLeftPoint();


}
