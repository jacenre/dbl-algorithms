import java.util.ArrayList;

public abstract class AbstractSkyline {

    public abstract void addRectangle(Rectangle rectangle);

    public abstract void removeRectangle(Rectangle rectangle);

    public abstract ArrayList<SegPoint> getCandidatePoints();

    public abstract int getMostLeftPoint();

}
