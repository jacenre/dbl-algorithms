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
     * Solution object containing the best solution found.
     */
    private ArrayList<Rectangle> bestSolutionState = null;

    /**
     * Find the optimal value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the associated {@link Solution} object
     */
    @Override
    Solution optimal(Parameters parameters) {
        // Try and solve it using all the solvers in the array
        for (AbstractSolver solver :
                solvers) {
            Solution solution = solver.solve(parameters);
            // If we found a better solution.
            if (bestSolution == null || solution.getArea() < bestSolution.getArea()) {
                bestSolution = solution;
                bestSolution.parameters.solvedBy = solver;
                bestSolutionState = cloneRectangleState(parameters.rectangles);
            }
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
