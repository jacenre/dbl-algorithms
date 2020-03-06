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
     *
     * @return this CompoundSolver
     */
    public CompoundSolver addSolver(AbstractSolver solver) {
        this.solvers.add(solver);
        return this;
    }

    /**
     * Solution object containing the best solution found.
     */
    private Solution bestSolution = null;

    /**
     * Returns the name of the subSolver that solved it.
     * @return the simple class name of the sub solver
     */
    @Override
    String getName() {
        return this.bestSolution.solvedBy.getName();
    }

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
    public Solution pack(Parameters parameters) {
        Parameters initialParameters = parameters.copy();

        // Try and getSolution it using all the solvers in the array
        for (AbstractSolver solver :
                solvers) {
            try {
                Solution solution = solver.getSolution(initialParameters.copy());
                // If we found a better solution.
                if (bestSolution == null || solution.getArea() < bestSolution.getArea()) {
                    bestSolution = solution.copy();
                }
            } catch (Exception e) {
                // ignore
            }
        }
        return bestSolution;
    }
}
