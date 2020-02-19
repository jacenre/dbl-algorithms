import java.util.ArrayList;

/**
 * Run multiple solvers on the given parameters and return the best solution.
 */
public class CompoundSolver extends AbstractSolver {

    /**
     * ArrayList containing all the solvers used.
     */
    private ArrayList<AbstractSolver> solvers = new ArrayList<>();

    /**
     * Add a solver to the CompoundSolver.
     */
    public void addSolver(AbstractSolver solver) {
        this.solvers.add(solver);
    }

    /**
     * Solution object containing the best solution found.
     */
    private Solution bestSolution = null;

    /**
     * Solves the given {@code parameters} using every {@code Solver} in {@code solvers}, returns the {@code Solution}
     * object associated with the best score found.
     * <p>
     * Ignores any thrown {@code IllegalArgumentException}. Deep copies the {@code parameters} before giving
     * it to a solver.
     * </p>
     *
     * @param parameters the {@code Parameters} to be used by the solver
     * @return a {@link Solution} object associated with the smallest area
     */
    @Override
    public Solution optimal(Parameters parameters) {
        Parameters initialParameters = parameters.copy();

        // Try and solve it using all the solvers in the array
        for (AbstractSolver solver :
                solvers) {
            try {
                Solution solution = solver.solve(initialParameters.copy());
                // If we found a better solution.
                if (bestSolution == null || solution.getArea() < bestSolution.getArea()) {
                    bestSolution = solution.copy();
                }
            } catch (IllegalArgumentException e) {
                // Ignore?
            }
        }
        return bestSolution;
    }
}
