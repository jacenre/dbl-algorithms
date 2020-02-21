/**
 * First runs FFT and then TLS to 'compress' the first result.
 */
public class CompressionSolver extends AbstractSolver {
    /**
     * Find the pack value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the associated {@link Solution} object
     */
    @Override
    Solution pack(Parameters parameters) throws IllegalArgumentException{
        FirstFitSolver firstFitSolver = new FirstFitSolver();
        SimpleTopLeftSolver simpleTopLeftSolver = new SimpleTopLeftSolver();

        Solution solution = firstFitSolver.getSolution(parameters);
        solution = simpleTopLeftSolver.getSolution(solution.parameters);

        return new Solution(solution.parameters, this);
    }
}
