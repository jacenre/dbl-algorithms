import java.util.ArrayList;
import java.util.List;

/**
 * Solver algorithm adapted from the BL-algorithm, where the height has to be fixed.
 */
public class TopLeftSolver extends SimpleTopLeftSolver {
    int binWidth = 0;

    /**
     * Find the pack value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the associated {@link Solution} object
     */
    Solution pack(Parameters parameters) throws IllegalArgumentException {
        if (parameters.rectangles.size() > 2000) {
//            throw new IllegalArgumentException("Too many rectangles");
        }
        // Put the first rectangle in the top left corner
        parameters.rectangles.get(0).x = 0;
        parameters.rectangles.get(0).y = 0;
        binWidth = parameters.rectangles.get(0).width;

        for (int i = 1; i < parameters.rectangles.size(); i++) {
            // Put the rectangle in the bottom right corner
            Rectangle rect = parameters.rectangles.get(i);
            rect.x = binWidth;
            rect.y = parameters.height - rect.height;
            move(rect, parameters.rectangles.subList(0, i));
            binWidth = Math.max(binWidth, rect.x + rect.width);
        }
        return new Solution(parameters, this);
    }

    /**
     * Move up until there is a possibility to move left.
     */
    @Override
    protected void moveUp(Rectangle rect, List<Rectangle> rectangles) {
        rect.y--;
    }
}
