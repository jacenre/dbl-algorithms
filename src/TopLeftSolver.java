import java.util.ArrayList;
import java.util.List;

/**
 * Solver algorithm adapted from the BL-algorithm, where the height has to be fixed.
 * This version corresponds with the "improved" BL-algorithm, where priority is given
 * to moving to the left if possible, while the SimpleTopLeftSolver alternates between going left and up.
 * This means that SimpleTopLeftSolver is much, much faster, but TopLeftSolver has a better rate of solution.
 */
public class TopLeftSolver extends SimpleTopLeftSolver {
    /**
     * Instead of going step by step, this method looks at what rectangles are
     * blocking it from going all the way to the left, and move to just the right side of them.
     */
    @Override
    protected void moveUp(Rectangle rect, List<Rectangle> rectangles) {
        rect.y--;
    }
}
