/**
 * Abstract class for the solver
 */
abstract class AbstractSolver {
    public HeightSupport[] heightSupport = new HeightSupport[]{HeightSupport.FIXED, HeightSupport.FREE};

    /**
     * Solves for the given parameters
     * @param parameters The parameters to be used by the solver.
     */
    Solution solve(Parameters parameters) {
        // Create a new solution for this solve.
        Solution solution = this.optimal(parameters);

        // report(solution);

        return solution;
    };

    /**
     * Find the optimal value for the parameters without doing any other output.
     * @param parameters The parameters to be used by the solver.
     * @return Returns the associated {@link Solution} object
     * @throws IllegalArgumentException If tasked with an incompatible parameter object for this solver.
     */
    abstract Solution optimal(Parameters parameters) throws IllegalArgumentException;
}

enum HeightSupport {
    FIXED,
    FREE
}