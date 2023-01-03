package jacenre.dbla;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * First runs FFT and then TLS to 'compress' the first result.
 */
public class CompressionSolver extends AbstractSolver {
    @Override
    Set<Util.HeightSupport> getHeightSupport() {
        return new HashSet<>(Collections.singletonList(Util.HeightSupport.FIXED));
    }

    @Override
    public boolean canSolveParameters(Parameters parameters) {
        boolean superResult = super.canSolveParameters(parameters);
        if (!superResult) {
			return false;
		}
        return parameters.rectangles.size() <= 2000;
    }

    /**
     * Find the pack value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the associated {@link Solution} object
     */
    @Override
    Solution pack(Parameters parameters) throws IllegalArgumentException {
        FirstFitSolver firstFitSolver = new FirstFitSolver();
        SimpleTopLeftSolver simpleTopLeftSolver = new SimpleTopLeftSolver();

        Solution solution = firstFitSolver.getSolution(parameters);
        solution = simpleTopLeftSolver.getSolution(solution.parameters);

        return new Solution(solution.parameters, this);
    }
}
