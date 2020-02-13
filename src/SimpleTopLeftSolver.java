import java.util.ArrayList;
import java.util.List;

/**
 * Solver algorithm adapted from the BL-algorithm, where the height has to be fixed.
 * To be used in a genetic algorithm due to its speed.
 */
public class SimpleTopLeftSolver extends AbstractSolver {
    int binWidth = 0;

    /**
     * Find the optimal value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the associated {@link Solution} object
     */
    @Override
    Solution optimal(Parameters parameters) throws IllegalArgumentException {
        if (!parameters.heightVariant.equals("fixed")) {
            throw new IllegalArgumentException("TopLeftSolver only works when the height is fixed.");
        }
        if (parameters.rectangles.size() > 10000) {
            return new Solution(Integer.MAX_VALUE, Integer.MAX_VALUE, parameters);
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
        return new Solution(binWidth, parameters.height, parameters);
    }

    protected void move(Rectangle rect, List<Rectangle> rectangles) {
        if (!canMoveLeft(rect, rectangles) && !canMoveUp(rect, rectangles)) {
            return;
        }
        while (canMoveLeft(rect, rectangles)) {
            moveLeft(rect, rectangles);
        }
        while (canMoveUp(rect, rectangles)) {
            moveUp(rect, rectangles);
            if (canMoveLeft(rect, rectangles)) {
                moveLeft(rect, rectangles);
            }
        }
    }

    /**
     * Instead of going step by step, this method looks at what rectangles are
     * blocking it from going all the way to the left, and move to just the right side of them.
     * This doesn't work for moveUp, because you always want to move left whenever you can.
     */
    protected void moveLeft(Rectangle rect, List<Rectangle> rectangles) {
        rect.x = 0;
        boolean intersects;
        // Check intersection with all placed rectangles
        do {
            intersects = false;
            for (Rectangle rectangle : rectangles) {
                if (rect.intersects(rectangle)) {
                    intersects = true;
                    rect.x = Math.max(rect.x, rectangle.x + rectangle.width);
                }
            }
        } while (intersects);
    }

    /**
     * Move up until there is a possibility to move left.
     */
    protected void moveUp(Rectangle rect, List<Rectangle> rectangles) {
        int prevY = rect.y;
        rect.y = 0;
        Rectangle path = new Rectangle(rect.x, 0, rect.width, prevY);
        for (Rectangle rectangle : rectangles) {
            if (rectangle.intersects(path) &&
                    rectangle.y + rectangle.height > rect.y + rect.height) {
                rect.y = rectangle.y + rectangle.height;
            }
        }
    }

    /** Check if the rectangle can move to its left */
    protected boolean canMoveLeft(Rectangle rect, List<Rectangle> rectangles) {
        if (rect.x <= 0) return false;
        rect.x--;
        // Check intersection with all placed rectangles
        for (Rectangle rectangle : rectangles) {
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
            if (rect.intersects(rectangle)) {
                rect.y++;
                return false;
            }
        }
        rect.y++;
        return true;
    }

    /** Check intersection of rect with all placed rectangles. */
    protected List<Rectangle> findIntersections(Rectangle rect, List<Rectangle> rectangles) {
        List<Rectangle> rects = new ArrayList<>();
        for (Rectangle rectangle : rectangles) {
            if (rect.intersects(rectangle)) {
                rects.add(rectangle);
            }
        }
        return rects;
    }
}
