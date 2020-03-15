import java.util.*;

/**
 * Solver algorithm adapted from the BL-algorithm, where the height has to be fixed.
 */
public class TopLeftSolver extends SimpleTopLeftSolver {

    @Override
    Set<Util.HeightSupport> getHeightSupport() {
        return new HashSet<>(Collections.singletonList(Util.HeightSupport.FIXED));
    }

    /**
     * Find the pack value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the associated {@link Solution} object
     */
    @Override
    Solution pack(Parameters parameters) throws IllegalArgumentException {
        if (parameters.rectangles.size() > 2000) {
            // Return a trivial solution
            int x = 0;
            for (Rectangle rectangle: parameters.rectangles ) {
                rectangle.x = x;
                x += rectangle.width;
                rectangle.place(true);
            }
            return new Solution(parameters, this);
        }
        Solution sol = super.pack(parameters);
        return new Solution(sol.parameters, this);
    }

    /**
     * Move up until there is a possibility to move left.
     */
    @Override
    protected void moveUp(Rectangle rect, List<Rectangle> rectangles) {
        rect.y = Math.max(0, rect.y - rect.height);
        for (Rectangle rectangle : rectangles) {
            if (rectangle.getId().equals(rect.getId())) break;
            if (rectangle.isPlaced() && rect.intersects(rectangle)) {
                rect.y = Math.max(rect.y, rectangle.y + rectangle.height);
            }
        }
    }
}
