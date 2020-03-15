import java.util.*;

/**
 * Solver algorithm adapted from the BL-algorithm, where the height has to be fixed.
 * To be used in a genetic algorithm due to its speed.
 */
public class SimpleTopLeftSolver extends AbstractSolver {
    int binWidth = 0;

    @Override
    Set<Util.HeightSupport> getHeightSupport() {
        return new HashSet<>(Collections.singletonList(Util.HeightSupport.FIXED));
    }

    @Override
    public boolean canSolveParameters(Parameters parameters) {
        boolean superResult = super.canSolveParameters(parameters);
        if (!superResult) return false;
        if (parameters.rectangles.size() > 2000 && (
                parameters.heightVariant == Util.HeightSupport.FREE || parameters.freeHeightUtil)) return false;
        return parameters.rectangles.size() <= 5000;
    }

    /**
     * Find the pack value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the associated {@link Solution} object
     */
    @Override
    Solution pack(Parameters parameters) throws IllegalArgumentException {
        Util.animate(parameters, this);

        // Sort the array from large to small
        parameters.rectangles.sort((o1, o2) -> (o2.height) - (o1.height));

        // Put the first rectangle in the top left corner
        parameters.rectangles.get(0).x = 0;
        parameters.rectangles.get(0).y = 0;
        parameters.rectangles.get(0).place(true);
        binWidth = parameters.rectangles.get(0).width;

        for (int i = 1; i < parameters.rectangles.size(); i++) {
            // Put the rectangle in the bottom right corner
            Rectangle rect = parameters.rectangles.get(i);
            rect.place(true);
            Util.animate();
            rect.x = binWidth;
            if (parameters.rotationVariant) {
                if (rect.height > parameters.height) {
                    rect.rotate();
                }
            }
            rect.y = parameters.height - rect.height;
            move(rect, parameters.rectangles);
            binWidth = Math.max(binWidth, rect.x + rect.width);
        }

        return new Solution(parameters, this);
    }

    protected void move(Rectangle rect, List<Rectangle> rectangles) {
        if (!canMoveLeft(rect, rectangles) && !canMoveUp(rect, rectangles)) {
            return;
        }
        if (canMoveLeft(rect, rectangles)) {
            moveLeft(rect, rectangles);
        }
        while (canMoveUp(rect, rectangles)) {
            Util.animate();
            moveUp(rect, rectangles);
            if (canMoveLeft(rect, rectangles)) {
                moveLeft(rect, rectangles);
            }
        }
        Util.animate();
        rect.place(true);
    }

    /**
     * Instead of going step by step, this method looks at what rectangles are
     * blocking it from going all the way to the left, and move to just the right side of them.
     */
    protected void moveLeft(Rectangle rect, List<Rectangle> rectangles) {
        Util.moveLeft(rect, rectangles);
    }

    /**
     * Move up until there is a possibility to move left.
     */
    protected void moveUp(Rectangle rect, List<Rectangle> rectangles) {
        Util.moveUp(rect, rectangles);
    }

    /** Check if the rectangle can move to its left */
    protected boolean canMoveLeft(Rectangle rect, List<Rectangle> rectangles) {
        if (rect.x <= 0) return false;
        rect.x--;
        // Check intersection with all placed rectangles
        for (Rectangle rectangle : rectangles) {
            if (rect.getId().equals(rectangle.getId())) break;
            if (rect.intersects(rectangle)) {
                rect.x++;
                return false;
            }
        }
        rect.x++;
        return true;
    }

    /** Check if the rectangle can move up */
    protected boolean canMoveUp(Rectangle rect, List<Rectangle> rectangles) {
        if (rect.y <= 0) return false;
        rect.y--;
        // Check intersection with all placed rectangles
        for (Rectangle rectangle : rectangles) {
            if (rect.getId().equals(rectangle.getId())) break;
            if (rect.intersects(rectangle)) {
                rect.y++;
                return false;
            }
        }
        rect.y++;
        return true;
    }
}
