package jacenre.dbla;
import java.util.*;

/**
 * Solver algorithm adapted from the BL-algorithm, where the height has to be fixed.
 */
public class TopLeftSolver extends SimpleTopLeftSolver {

    public TopLeftSolver(boolean allowInputSorting) {
        super(allowInputSorting);
    }
    public TopLeftSolver() {
        super();
    }

    @Override
    Set<Util.HeightSupport> getHeightSupport() {
        return new HashSet<>(Collections.singletonList(Util.HeightSupport.FIXED));
    }

    @Override
    public boolean canSolveParameters(Parameters parameters) {
        boolean superResult = super.canSolveParameters(parameters);
        if (!superResult) return false;
        return parameters.rectangles.size() <= 500;
    }

    /**
     * Find the pack value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the associated {@link Solution} object
     */
    @Override
    Solution pack(Parameters parameters) throws IllegalArgumentException {
        Solution sol = super.pack(parameters);
        return new Solution(sol.parameters, this);
    }

    /**
     * Move up until there is a possibility to move left.
     */
    @Override
    protected void moveUp(Rectangle rect, List<Rectangle> rectangles) {
        if (rect.y <= 0) {
            Util.moveUp(rect, rectangles);
        } else {
            rect.y = Math.max(0, rect.y - rect.height);
            for (Rectangle rectangle : rectangles) {
                if (rectangle.getId().equals(rect.getId())) break;
                if (rectangle.isPlaced() && rect.intersects(rectangle)) {
                    rect.y = Math.max(rect.y, rectangle.y + rectangle.height);
                }
            }
        }
    }
}
