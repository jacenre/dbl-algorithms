import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract Solver class
 * <p>
 * The main class that will be used for solving the Bin Packing problem.<br>
 * The intention is that the PackingSolver will call {@link #solve(Parameters)} on a concrete solver.<br>
 * To implement a {@code Solver} it suffices to implement the hook method {@link #optimal(Parameters)}.<br>
 * </p>
 * <p>
 * To specify what type of Bin Packing problems the {@code Solver} can handle use {@link Util.HeightSupport}.<br>
 * Throw an {@code IllegalArgumentException} if the given {@code Parameters} violate the {@code Solver} preconditions.<br>
 * </p>
 */
public abstract class AbstractSolver {

    /**
     * Gets all the supported height variants.
     * <p>
     * By default both {@code FREE} and {@code FIXED} are enabled.
     *
     * @return a {@code Set} containing all the supported height variants.
     * @see Util.HeightSupport
     * </p>
     */
    Set<Util.HeightSupport> getHeightSupport() {
        return new HashSet<>(Arrays.asList(Util.HeightSupport.FREE, Util.HeightSupport.FIXED));
    }

    /**
     * Returns a {@code Solution} for the given {@code Parameters}
     * <p>
     * Contains the template code for most solvers. By default it will rotate any rectangle
     * that are to high to fit in a fixed box, if applicable. It will also check if the {@code Parameter }
     * heightVariant and the supported height variants of this {@code Solver} match.
     * </p>
     *
     * @param parameters the parameters for which to solve
     * @return a {@code Solution} object containing the results
     * @throws IllegalArgumentException if the Solver cannot solve the given parameters
     */
    public Solution solve(Parameters parameters) throws IllegalArgumentException {
        if (!getHeightSupport().contains(parameters.heightVariant)) {
            throw new IllegalArgumentException("Unsupported height variant");
        }

        // General rule, if rotating make sure that every rectangle fits.
        if (parameters.rotationVariant) {
            for (Rectangle rectangle :
                    parameters.rectangles) {
                if (rectangle.height > parameters.height) {
                    rectangle.rotate();
                }
            }
        }

        // Create a new solution for this solve.
        Solution solution = this.optimal(parameters);

        // report(solution);

        return solution;
    }

    /**
     * Hook method for implementing a {@code Solver}.
     * <p>
     * The intention is that the PackingSolver calls {@link #solve(Parameters)} which will in turn call this function.
     * The reasoning is that the solve function can contain all the Template code needed for the setup and the solve
     * itself.
     * </p>
     * <p>
     * To solve for a {@code Parameters} object you have to set all the x and y coordinates for all the rectangles in
     * {@link Parameters#rectangles} in such a way that there is no overlap. The {@code Parameters} object in the
     * final {@code Solution} object should be the same or deep copied over via {@link Parameters#copy()}.
     * As of now the {@code PackingSolver} does not ensure that a given {@code Solution} is valid and thus this
     * responsibility lies in the hand of the programming implementing the {@code optimal} function.
     * </p>
     *
     * @param parameters the {@code Parameters} to be used by the solver
     * @return the associated {@link Solution} object containing the results
     */
    abstract Solution optimal(Parameters parameters);
}

