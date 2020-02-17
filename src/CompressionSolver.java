/**
 * First runs FFT and then TLS to 'compress' the first result.
 */
public class CompressionSolver extends AbstractSolver {
    /**
     * Find the optimal value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the associated {@link Solution} object
     */
    @Override
    Solution optimal(Parameters parameters) {
        FirstFitSolver firstFitSolver = new FirstFitSolver();
        TopLeftSolver topLeftSolver = new TopLeftSolver();

        Solution solution = firstFitSolver.solve(parameters);
        solution = topLeftSolver.solve(solution.parameters);

        return new Solution(solution.parameters, this);
    }
}