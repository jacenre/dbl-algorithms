import java.util.ArrayList;

/**
 * Abstract class for the solver
 */
abstract class AbstractSolver {


    /**
     * Solution object containing the best solution found.
     */
    private ArrayList<Rectangle> bestSolutionState = null;

    Solution solve(Parameters parameters) {
        if (parameters.heightVariant.equals("fixed")) {
            return this.solveFixedHeight(parameters);
        } else { // heightVariant == "free"
            return this.solveFreeHeight(parameters);
        }
    }

    /**
     * Find the optimal value for the parameters without doing any other output.
     * @param parameters The parameters to be used by the solver.
     * @return Returns the associated {@link Solution} object
     */
    abstract Solution solveFixedHeight(Parameters parameters);

    /**
     * Tries different fixed heights. Starts with a maximum height, then halfs it and checks if it is smaller.
     *  /TODO find a good algorithm for this optimization problem
     * @param parameters
     * @return optimalSolution
     */
    Solution solveFreeHeight(Parameters parameters) {
        int height = 0; // The absolute max height that it could have
        for (Rectangle rectangle : parameters.rectangles) {
            if (rectangle.width > rectangle.height) {
                height += rectangle.width;
            } else {
                height += rectangle.height;
            }
        }

        height = Math.min(height, 46340); // Otherwise integer overflow, 46340 = sqrt(Integer.MAX_VALUE)
        parameters.height = height;
        Solution bestSolution = null;
        Solution solution = solveFixedHeight(parameters);

        while((bestSolution == null || solution.getArea() < bestSolution.getArea())) {
            bestSolution = solution;
            bestSolutionState = cloneRectangleState(parameters.rectangles);
            height /= 2;
            parameters.height = height;
            solution = solveFixedHeight(parameters);
        }

        parameters.rectangles = bestSolutionState;
        return bestSolution;
    }

    private static ArrayList<Rectangle> cloneRectangleState(ArrayList<Rectangle> rects) {
        ArrayList<Rectangle> rectangles = new ArrayList<>();
        for (Rectangle rect:
                rects) {
            rectangles.add(new Rectangle(rect));
        }
        return rectangles;
    }


}