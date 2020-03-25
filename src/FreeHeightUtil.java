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
     * @throws IllegalArgumentException if subsolver does not support free height
     */
    Solution pack(Parameters parameters) {
        if (!this.subSolver.getHeightSupport().contains(Util.HeightSupport.FREE)) {
            throw new IllegalArgumentException("Doesn't support free height");
        }

        parameters.freeHeightUtil = true; // make sure that (compound solver)

        Util.animate(parameters, subSolver);

        // TODO Find something less dumb, like basing the sampling rate on the HEIGHT.
        // TODO Find the maximum number solves that is < 30 sec runtime.

        // Set the amount of checks to be done
        final int numChecks = 500;
        bestSolution = localMinimaFinder(parameters, numChecks);

        Util.animate(parameters, subSolver);

        bestSolution.parameters.freeHeightUtil = false; // change as if not processed by freeHeightUtil
        bestSolution.parameters.heightVariant = Util.HeightSupport.FREE;
        return bestSolution;
    }

    /**
     * Return best solution dependent on the height.
     * @param parameters of the problem
     * @param numChecks number of checks to do at most
     * @return best Solution found
     */
    Solution localMinimaFinder(Parameters parameters, int numChecks) {
        // Starting conditions
        int minimumHeight = Util.largestRect(parameters);
        int maximumHeight = Util.sumHeight(parameters);

        int startRange = minimumHeight;
        int stopRange = maximumHeight;

        // number of possible heights that could be used to solve
        int numPossibleHeights = maximumHeight - minimumHeight;

        final double L1 = Math.log((float) 1/numPossibleHeights); // for simplification of expression of checksPerIteration
        //final double numRecursions = (L1/(MathUtil.LambertMinusOne(2*L1/numChecks))); // approximate number of recursions that will be made
        final double checksPerIteration = (numChecks * MathUtil.LambertMinusOne(L1/numChecks)/L1);

        int stepSize = (int) (numPossibleHeights/checksPerIteration);

        // set current bests with the maximum possible height
        double currentBestHeight = maximumHeight;
        parameters.heightVariant = Util.HeightSupport.FIXED;
        parameters.height = (int) currentBestHeight;
        bestSolution = subSolver.pack(parameters.copy());

        // create empty arrays in which to store data to plot
        int[] chartYData = new int[(int) ((stopRange - startRange) * stepSize) + 1];
        int[] chartXData = new int[(int) ((stopRange - startRange) * stepSize) + 1];
        int chartIndex = 0; // determines where to place data in chart arrays

        boolean firstIteration = true;

        do {
            System.out.println(stepSize);
            for (double newHeight = startRange + stepSize; newHeight <= stopRange - stepSize; newHeight += stepSize) {
                Parameters params = parameters.copy();
                params.height = (int) newHeight;
                Solution newSolution = subSolver.pack(params);

                if (firstIteration) { // only plot the data of the first iteration
                    chartXData[chartIndex] = (int) newHeight;
                    chartYData[chartIndex] = newSolution.getArea();
                }

                if (newSolution.getArea(true) < bestSolution.getArea(true)) {
                    // update bestSolution
                    currentBestHeight = (int) newHeight;
                    bestSolution = newSolution;
                }
                chartIndex++;

            }
            firstIteration = false;

            // update ranges around the best found value
            startRange = (int) Math.max(minimumHeight, currentBestHeight - stepSize);
            stopRange = (int) Math.min(maximumHeight, currentBestHeight + stepSize);

            // update stepSize
            stepSize = (int) ((stopRange - startRange)/checksPerIteration);
        } while (stepSize <= 0);

        bestSolution.chartData = new int[][]{chartXData, chartYData};
        return bestSolution;
    }
}
