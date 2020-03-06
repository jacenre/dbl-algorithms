import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Util that allows any {@Code Util.HeightSupport.FIXED} to be turned into a {@Code Util.HeightSupport.FREE} solver
 * using local minima finder.
 */
public class FreeHeightUtil {

    /**
     * The AbstractSolver used during {@link #localMinimaFinder(Parameters, double)}, by default {@link FirstFitSolver}.
     */
    private AbstractSolver subSolver;

    /**
     * Solution object containing the best solution found.
     */
    private Solution bestSolution = null;

    /**
     * Constructor that sets the {@code subSolver}
     *
     * @param subSolver the AbstractSolver to use
     * @see #localMinimaFinder(Parameters, double)
     */
    FreeHeightUtil(AbstractSolver subSolver) {
        this.subSolver = subSolver;
    }


    /**
     * Find the pack value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the pack area found by this solver.
     */
    Solution pack(Parameters parameters) {
        Util.animate(parameters, subSolver);

        if (parameters.rectangles.size() > 100) {
            bestSolution = localMinimaFinder(parameters, 0.01);
        } else {
            bestSolution = localMinimaFinder(parameters, 1);
        }

        Util.animate(parameters, subSolver);

        bestSolution.parameters.heightVariant = Util.HeightSupport.FREE;
        return bestSolution;
    }

    Solution localMinimaFinder(Parameters parameters, double samplingRate) {
        // Starting conditions
        double startRange = largestRect(parameters);
        double stopRange = sumHeight(parameters);
        double searchSize = 1 / samplingRate;

        int minima = 0;

        // ensure that best solution is never null
        parameters.heightVariant = Util.HeightSupport.FIXED;
        bestSolution = subSolver.getSolution(parameters.copy());

        while (stopRange - startRange > 1) {
            for (double i = startRange; i <= stopRange; i += searchSize) {

                Parameters params = parameters.copy();
                params.height = (int) i;
                System.out.println(params.heightVariant);
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
            height = Math.max(rectangle.height, height);
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
