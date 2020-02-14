import java.lang.reflect.Parameter;
import java.util.ArrayList;

/**
 * Intent: Run multiple solvers on the parameters and return the best solution.
 */
public class CompoundSolver extends AbstractSolver {

    /**
     * ArrayList containing all the solvers used.
     */
    private ArrayList<AbstractSolver> solvers = new ArrayList<>();

    /**
     * Add a solver to the CompoundSolver
     */
    public void addSolver(AbstractSolver solver) {
        this.solvers.add(solver);
    }

    /**
     * Solution object containing the best solution found.
     */
    private Solution bestSolution = null;

    /**
     * Find the optimal value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the associated {@link Solution} object
     */
    @Override
    Solution optimal(Parameters parameters) {
        /**
         * Solution object containing the best solution found.
         */
        ArrayList<Rectangle> bestSolutionState = cloneRectangleState(parameters.rectangles);

        Parameters initialParameters = parameters.copy();

        // Try and solve it using all the solvers in the array
        for (AbstractSolver solver :
                solvers) {
            try {
                Solution solution = solver.solve(initialParameters.copy());
                System.out.println(solver.getClass().getSimpleName() + " found solution " + solution.getArea());
                // continue if solution is invalid.
//                if (solution.getRate() < 1) {
//                    continue;
//                }

                // If we found a better solution.
                if (bestSolution == null || solution.getArea() < bestSolution.getArea()) {
                    bestSolution = solution.copy();
                    bestSolutionState = cloneRectangleState(parameters.rectangles);
                }
            } catch (IllegalArgumentException e) {
                // Ignore?
            }
        }
        parameters.rectangles = bestSolutionState;
        return bestSolution;
    }

    public static ArrayList<Rectangle> cloneRectangleState(ArrayList<Rectangle> rects) {
        ArrayList<Rectangle> rectangles = new ArrayList<>();
        for (Rectangle rect:
                rects) {
            rectangles.add(new Rectangle(rect));
        }
        return rectangles;
    }
}
