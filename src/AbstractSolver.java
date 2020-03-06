import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract Solver class
 * <p>
 * The main class that will be used for solving the Bin Packing problem.<br>
 * The intention is that the PackingSolver will call {@link #getSolution(Parameters)} on a concrete solver.<br>
 * To implement a {@code Solver} it suffices to implement the hook method {@link #pack(Parameters)}.<br>
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
     * @param parameters the parameters for which to getSolution
     * @return a {@code Solution} object containing the results
     * @throws IllegalArgumentException if the Solver cannot getSolution the given parameters
     */
    public Solution getSolution(Parameters parameters) throws IllegalArgumentException {
        if (!getHeightSupport().contains(parameters.heightVariant)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    " does not support " + parameters.heightVariant);
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

        Solution solution; // Solution

        // Create a new solution for this getSolution.
        if (parameters.heightVariant.equals(Util.HeightSupport.FIXED)) {
            solution = this.pack(parameters);
        } else {
            Util.animate(parameters, this);
            Solution bestSolution;

            if (parameters.rectangles.size() > 100) {
                bestSolution = localMinimaFinder(parameters, 0.01);
            } else {
                bestSolution = localMinimaFinder(parameters, 1);
            }

            Util.animate(parameters, this);

            bestSolution.parameters.heightVariant = Util.HeightSupport.FREE;
            bestSolution.solvedBy = this;
            return bestSolution;
        }
        // report(solution);

        return solution;
    }

    Solution localMinimaFinder(Parameters parameters, double samplingRate) {
        // Starting conditions
        double startRange = largestRect(parameters);
        double stopRange = sumHeight(parameters);
        double searchSize = 1 / samplingRate;

        int minima = 0;

        // ensure that best solution is never null
        Solution bestSolution = pack(parameters.copy());
        parameters.heightVariant = Util.HeightSupport.FIXED;

        boolean firstIteration = true;
        int[] chartYData = new int[(int)((stopRange - startRange)/searchSize) + 1];
        int[] chartXData = new int[(int)((stopRange - startRange)/searchSize) + 1];
        int iter = 0;

        while (stopRange - startRange > 1) {
            for (double i = startRange; i <= stopRange; i += searchSize) {

                Parameters params = parameters.copy();
                params.height = (int) i;

                Solution newSolution = pack(params);

                if (firstIteration) {
                    chartXData[iter] = (int) i;
                    chartYData[iter] = newSolution.getArea();
                }

                if (bestSolution == null || newSolution.getArea(true) < bestSolution.getArea(true)) {
                    minima = (int) i;
                    bestSolution = newSolution;
                }
                iter++;
            }
            firstIteration = false;
            startRange = (int) Math.max(1, minima - searchSize);
            stopRange = (int) (minima + searchSize);
            searchSize = Math.max(searchSize / 2, 0);
        }

        bestSolution.chartData = new int[][]{chartXData, chartYData};

        return bestSolution;
    }

    /**
     * Hook method for implementing a {@code Solver}.
     * <p>
     * The intention is that the PackingSolver calls {@link #getSolution(Parameters)} which will in turn call this function.
     * The reasoning is that the getSolution function can contain all the Template code needed for the setup and the getSolution
     * itself.
     * </p>
     * <p>
     * To getSolution for a {@code Parameters} object you have to set all the x and y coordinates for all the rectangles in
     * {@link Parameters#rectangles} in such a way that there is no overlap. The {@code Parameters} object in the
     * final {@code Solution} object should be the same or deep copied over via {@link Parameters#copy()}.
     * As of now the {@code PackingSolver} does not ensure that a given {@code Solution} is valid and thus this
     * responsibility lies in the hand of the programming implementing the {@code pack} function.
     * </p>
     *
     * @param parameters the {@code Parameters} to be used by the solver
     * @return the associated {@link Solution} object containing the results
     */
    abstract Solution pack(Parameters parameters);

    /**
     * Returns the height of the largest rectangle in the parameters rectangle arrays.
     *
     * @param parameters the Parameters in which to search
     * @return the height of the largest rectangle
     */
    private int largestRect(Parameters parameters) {
        int height = 0;
        for (Rectangle rectangle :
                parameters.rectangles) {
            height = (rectangle.height > height)? rectangle.height: height;
        }
        return height;
    }

    /**
     * Returns the sum of all the heights in the parameters rectangle arrays.
     *
     * @param parameters the Parameters for which to sum
     * @return the sum of all the heights
     */
    private int sumHeight(Parameters parameters) {
        int sum = 0;
        for (Rectangle rectangle :
                parameters.rectangles) {
            sum += rectangle.height;
        }
        return sum;
    }
}

