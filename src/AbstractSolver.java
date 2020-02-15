import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class for the solver
 */
abstract class AbstractSolver {

    Set<HeightSupport> getHeightSupport() {
        return new HashSet<>(Arrays.asList(HeightSupport.FREE, HeightSupport.FIXED));
    }

    /**
     * Solves for the given parameters
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the associated {@link Solution} object.
     * @throws IllegalArgumentException If tasked with an incompatible parameter object for this solver.
     */
    Solution solve(Parameters parameters) throws IllegalArgumentException {
        if (!getHeightSupport().contains(parameters.heightVariant)) {
            throw new IllegalArgumentException("Unsupported height variant");
        }

        // Create a new solution for this solve.
        Solution solution = this.optimal(parameters);

        // report(solution);

        return solution;
    }

    ;

    /**
     * Find the optimal value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the associated {@link Solution} object
     */
    abstract Solution optimal(Parameters parameters);
}

