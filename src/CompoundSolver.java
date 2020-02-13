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
        // Copy the parameters
        Parameters parametersCopy = parameters;

        // Try and solve it using all the solvers in the array
        for (AbstractSolver solver :
                solvers) {
            Solution solution = solver.solve(parametersCopy);
            // If we found a better solution.
            if (bestSolution == null || solution.getArea() < bestSolution.getArea()) {
                bestSolution = solution;
            }
        }
        return bestSolution;
    }
}
