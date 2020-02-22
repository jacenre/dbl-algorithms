import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Free height version of the FirstFitSolver.
 */
public class FreeHeightSolver extends AbstractSolver {

    @Override
    Set<Util.HeightSupport> getHeightSupport() {
        return new HashSet<>(Arrays.asList(Util.HeightSupport.FREE));
    }

    /**
     * The AbstractSolver used during {@link #localMinimaFinder(Parameters, double)}, by default {@link FirstFitSolver}.
     */
    private AbstractSolver subSolver;

    /**
     * Default constructor for method overriding.
     */
    FreeHeightSolver() {
        this.subSolver = new CompoundSolver()
                .addSolver(new FirstFitSolver());
//                .addSolver(new ReverseFitSolver());
    }

    /**
     * Constructor that sets the {@code subSolver} to a different solver.
     *
     * @param subSolver the AbstractSolver to use
     * @see #localMinimaFinder(Parameters, double)
     */
    FreeHeightSolver(AbstractSolver subSolver) {
        this.subSolver = subSolver;
    }


    /**
     * Find the pack value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the pack area found by this solver.
     */
    @Override
    Solution pack(Parameters parameters) {
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

    Solution localMinimaFinder(Parameters parameters, double samplingRate) {
        // Starting conditions
        double startRange = largestRect(parameters);
        double stopRange = sumHeight(parameters);
        double searchSize = 1 / samplingRate;

        int minima = 0;

        // ensure that best solution is never null
        Solution bestSolution = subSolver.getSolution(parameters.copy());
        parameters.heightVariant = Util.HeightSupport.FIXED;

        while (stopRange - startRange > 1) {
            for (double i = startRange; i <= stopRange; i += searchSize) {

                Parameters params = parameters.copy();
                params.height = (int) i;

                Solution newSolution = subSolver.getSolution(params);

                if (bestSolution == null || newSolution.getArea(true) < bestSolution.getArea(true)) {
                    minima = (int) i;
                    bestSolution = newSolution;
                }

            }
            startRange = (int) Math.max(1, minima - searchSize);
            stopRange = (int) (minima + searchSize);
            searchSize = Math.max(searchSize / 2, 0);
        }

        return bestSolution;
    }

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
