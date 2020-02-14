/**
 * Abstract class for the solver
 */
abstract class AbstractSolver {
    public HeightSupport[] heightSupport = new HeightSupport[]{HeightSupport.FIXED, HeightSupport.FREE};

    /**
     * Solves for the given parameters
     * @param parameters The parameters to be used by the solver.
     */
    Solution solve(Parameters parameters){
        // Create a new solution for this solve.
        Solution solution = this.optimal(parameters);

        // report(solution);

        return solution;
    };

    /**
     * Find the optimal value for the parameters without doing any other output.
     * @param parameters The parameters to be used by the solver.
     * @return Returns the associated {@link Solution} object
     */
    abstract Solution optimal(Parameters parameters);
}

enum HeightSupport {
    FIXED,
    FREE
}