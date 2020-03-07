import java.util.ArrayList;

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
        if (!this.subSolver.getHeightSupport().contains(Util.HeightSupport.FREE)) {
            throw new IllegalArgumentException("Doesn't support free height");
        }

        // fixed the compound solver
        parameters.freeHeightUtil = true;

        Util.animate(parameters, subSolver);

        if (parameters.rectangles.size() > 100) {
            bestSolution = localMinimaFinder(parameters, 0.01);
        } else {
            bestSolution = localMinimaFinder(parameters, 1);
        }

        Util.animate(parameters, subSolver);

        bestSolution.parameters.freeHeightUtil = false;
        bestSolution.parameters.heightVariant = Util.HeightSupport.FREE;
        return bestSolution;
    }

    Solution localMinimaFinder(Parameters parameters, double samplingRate) {
        // Starting conditions
        double minimum = largestRect(parameters);
        double maximum = sumHeight(parameters);

        double startRange = minimum;
        double stopRange = maximum;
        System.out.println(minimum + ", " + maximum);
        double searchSize = 1 / samplingRate;
        int minima = 0;

        // ensure that best solution is never null
        parameters.heightVariant = Util.HeightSupport.FIXED;
        parameters.height = (int) stopRange;
        bestSolution = subSolver.pack(parameters.copy());

        boolean firstIteration = true;
        int[] chartYData = new int[(int) ((stopRange - startRange) / searchSize) + 1];
        int[] chartXData = new int[(int) ((stopRange - startRange) / searchSize) + 1];
        int iter = 0;

        while (stopRange - startRange > 1) {
            for (double i = startRange; i <= stopRange; i += searchSize) {
                Parameters params = parameters.copy();
                params.height = (int) i;
                Solution newSolution = subSolver.pack(params);

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
            startRange = (int) Math.max(minimum, minima - searchSize);
            stopRange = (int) Math.min(maximum, minima + searchSize);
            searchSize = Math.max(searchSize / 2, 0);
        }

        bestSolution.chartData = new int[][]{chartXData, chartYData};
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
            if (parameters.rotationVariant) {
                height = Math.max(rectangle.width, height);
            }
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
