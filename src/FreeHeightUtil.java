import java.util.ArrayList;

/**
 * Util that allows any {@code Util.HeightSupport.FIXED} to be turned into a {@code Util.HeightSupport.FREE} solver
 * using local minima finder.
 */
public class FreeHeightUtil {

    /**
     * The AbstractSolver used during {@link #localMinimaFinder(Parameters, double)}, by default {@link FirstFitSolver}.
     */
    private AbstractSolver subSolver;

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
     * @throws IllegalArgumentException if subsolver does not support free height
     */
    Solution pack(Parameters parameters) {
        if (!this.subSolver.getHeightSupport().contains(Util.HeightSupport.FREE)) {
            throw new IllegalArgumentException("Doesn't support free height");
        }

        // fixed the compound solver
        parameters.freeHeightUtil = true; // TODO properly document what this does

        Util.animate(parameters, subSolver);

        int MAX_SOLVE_COUNT = 150; // TODO find metric to base this number on

        // TODO Find something less dumb, like basing the sampling rate on the HEIGHT.
        // TODO Find the maximum number solves that is < 30 sec runtime.

        // set samplingRate
        double samplingRate;
        if (parameters.rectangles.size() < 100) {
            samplingRate = 1;
        } else {
            double solves = Util.sumHeight(parameters) - Util.largestRect(parameters);
            samplingRate = MAX_SOLVE_COUNT / solves;
        }
        Solution bestSolution = localMinimaFinder(parameters, samplingRate);

        Util.animate(parameters, subSolver);

        bestSolution.parameters.freeHeightUtil = false; // TODO properly document what his does
        bestSolution.parameters.heightVariant = Util.HeightSupport.FREE;
        return bestSolution;
    }

    /**
     * Return best solution dependent on the height.
     * @param parameters of the problem
     * @param samplingRate // TODO replace with an explainable number
     * @return best Solution found
     */
    Solution localMinimaFinder(Parameters parameters, double samplingRate) {
        // Starting conditions
        double minimumHeight = Util.largestRect(parameters);
        double maximumHeight = Util.sumHeight(parameters);

        double startRange = minimumHeight;
        double stopRange = maximumHeight;
        double searchSize = 1 / samplingRate;

        // set current bests with the maximum possible height
        double currentBestHeight = maximumHeight / 2;
        parameters.heightVariant = Util.HeightSupport.FIXED;
        parameters.height = (int) currentBestHeight;
        Solution bestSolution = subSolver.pack(parameters.copy());

        // create empty arrays in which to store data to plot
        int[] chartYData = new int[(int) ((stopRange - startRange) / searchSize) + 1];
        int[] chartXData = new int[(int) ((stopRange - startRange) / searchSize) + 1];
        int chartIndex = 0; // determines where to place data in chart arrays

        boolean firstIteration = true;

        while (stopRange - startRange > 1) {
            for (double newHeight = startRange + searchSize; newHeight < stopRange; newHeight += searchSize) {
                Parameters params = parameters.copy();
                params.height = (int) newHeight;
                Solution newSolution = subSolver.pack(params);

                if (newSolution == null) continue;

                if (firstIteration) { // only plot the data of the first iteration
                    chartXData[chartIndex] = (int) newHeight;
                    chartYData[chartIndex] = newSolution.getArea();
                }

                // Check if null (edge cases)
                if (bestSolution == null) {
                    // update bestSolution
                    currentBestHeight = (int) newHeight;
                    bestSolution = newSolution;
                } else if (newSolution.getArea(true) < bestSolution.getArea(true)) {
                    // update bestSolution
                    currentBestHeight = (int) newHeight;
                    bestSolution = newSolution;
                }
                chartIndex++;

            }
            firstIteration = false;

            // update ranges around the best found value
            startRange = (int) Math.max(minimumHeight, currentBestHeight - searchSize);
            stopRange = (int) Math.min(maximumHeight, currentBestHeight + searchSize);
            // TODO do'nt just use half of the search size above (or do, it might be oke)
            searchSize = Math.max(searchSize / 2, 0);
        }

        bestSolution.chartData = new int[][]{chartXData, chartYData};
        return bestSolution;
    }
}
