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
     * Solves for the given parameters and returns a solution object encapsulating the result
     * @param parameters The parameters to be used by the solver.
     * @return A {@link Solution} object encapsulating the solution.
     */
    Solution solution(Parameters parameters) {
        // Create a new solution for this solve.
        Solution solution = new Solution(parameters);

        // Use the concrete implementation of optimal to get the results
        int[] optimal = this.optimal(parameters);

        // TODO possible change this to a coordinate like object.
        solution.setHeight(optimal[0]);
        solution.setWidth(optimal[1]);

        return solution;
    }

    /**
     * Find the optimal value for the parameters without doing any other output.
     * @param parameters The parameters to be used by the solver.
     * @return Returns an int[] array where int[0] is the height of the solution and int[1] the width.
     */
    abstract int[] optimal(Parameters parameters);
}