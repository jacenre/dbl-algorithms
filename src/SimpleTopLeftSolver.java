import java.util.*;

/**
 * Solver algorithm adapted from the BL-algorithm, where the height has to be fixed.
 * To be used in a genetic algorithm due to its speed.
 */
public class SimpleTopLeftSolver extends AbstractSolver {
    int binWidth = 0;

    public SimpleTopLeftSolver(boolean allowInputSorting) {
        super(allowInputSorting);
    }
    public SimpleTopLeftSolver() {
        super();
    }

    @Override
    Set<Util.HeightSupport> getHeightSupport() {
        return new HashSet<>(Arrays.asList(Util.HeightSupport.FREE, Util.HeightSupport.FIXED));
    }

    @Override
    public boolean canSolveParameters(Parameters parameters) {
        boolean superResult = super.canSolveParameters(parameters);
        if (!superResult) return false;
        if (parameters.rectangles.size() > 500 && (
                parameters.heightVariant == Util.HeightSupport.FREE || parameters.freeHeightUtil)) return false;
        return parameters.rectangles.size() <= 900;
    }

    /**
     * Find the pack value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.|
     * @return Returns the associated {@link Solution} object
     */
    @Override
    Solution pack(Parameters parameters) throws IllegalArgumentException {
        // Get a trivial solution
        int x = 0;
        for (Rectangle rectangle: parameters.rectangles ) {
            rectangle.x = x;
            x += rectangle.width;
            rectangle.place(true);
        }
        Solution trivialSolution = new Solution(parameters, this);

        Util.animate(parameters, this);

        // Sort the array from large to small
        if (allowInputSorting) {
            parameters.rectangles.sort((o1, o2) -> (o2.height) - (o1.height));
        }

        // Get 50 solutions based on rotating differently
        Solution bestSolution = trivialSolution;
        Parameters initialParameters = parameters.copy();

        for (int n = 0; n < 5; n++) {
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
                if (parameters.rotationVariant && (new Random()).nextBoolean() && rect.width < parameters.height) {
                    rect.rotate();
                }
                rect.x = binWidth;
                rect.y = parameters.height - rect.height;
                move(rect, parameters.rectangles);
                binWidth = Math.max(binWidth, rect.x + rect.width);
            }
            Solution sol = new Solution(parameters, this);
            bestSolution = sol.getArea() < bestSolution.getArea() ? sol : bestSolution;
            parameters = initialParameters.copy();
        }


        return bestSolution;
    }

    protected void move(Rectangle rect, List<Rectangle> rectangles) {
        if (!canMoveLeft(rect, rectangles) && !canMoveUp(rect, rectangles)) {
            return;
        }
        if (canMoveLeft(rect, rectangles)) {
            moveLeft(rect, rectangles);
        }
        while (canMoveUp(rect, rectangles)) {
            moveUp(rect, rectangles);
            if (canMoveLeft(rect, rectangles)) {
                moveLeft(rect, rectangles);
            }
        }
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
