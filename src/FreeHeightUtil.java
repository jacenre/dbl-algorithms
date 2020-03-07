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

        int MAX_SOLVE_COUNT = 150; // ? random number tbf.

        // TODO Find something less dumb, like basing the sampling rate on the HEIGHT.
        // TODO Find the maximum number solves that is < 30 sec runtime.
        if (parameters.rectangles.size() < 100) {
            bestSolution = localMinimaFinder(parameters, 1);
        } else {
            double solves = Util.sumHeight(parameters) - Util.largestRect(parameters);
            bestSolution = localMinimaFinder(parameters, MAX_SOLVE_COUNT / solves);
        }

        Util.animate(parameters, subSolver);

        bestSolution.parameters.freeHeightUtil = false;
        bestSolution.parameters.heightVariant = Util.HeightSupport.FREE;
        return bestSolution;
    }

    Solution localMinimaFinder(Parameters parameters, double samplingRate) {
        // Starting conditions
        double minimum = Util.largestRect(parameters);
        double maximum = Util.sumHeight(parameters);

        double startRange = minimum;
        double stopRange = maximum;
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
}
