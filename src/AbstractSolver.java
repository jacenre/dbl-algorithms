/**
 * Abstract class for the solver
 */
abstract class AbstractSolver {

    /**
     * Solves for the given parameters
     * @param parameters The parameters to be used by the solver.
     */
    abstract void solve(Parameters parameters);

    /**
     * Find the optimal value for the parameters without doing any other output.
     * @param parameters The parameters to be used by the solver.
     * @return Returns the optimal area found by this solver.
     */
    abstract int[] optimal(Parameters parameters);
}