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
        final int minimumHeight = Util.largestRect(parameters);
        final int maximumHeight = Util.sumHeight(parameters);

        // range over which to check for best solution (will get smaller each recursion)
        int startRange = minimumHeight;
        int stopRange = maximumHeight;

        // number of possible heights that could be used to solve
        int numPossibleHeights = maximumHeight - minimumHeight;
        System.out.println("Possible outputs: " + numPossibleHeights);


        // For the math behind this, refer to Tristan Trouwen (or maybe the report in a later stage)
        final double L1 = Math.log((float) 1/numPossibleHeights); // for simplification of expression of checksPerIteration
        final double numRecursions = (L1/(MathUtil.LambertMinusOne(2*L1/numChecks))); // approximate number of recursions that will be made
        System.out.println("Approximate number of recursions: " + numRecursions);

        final double checksPerIteration = (numChecks * MathUtil.LambertMinusOne(L1/numChecks)/L1);
        System.out.println("Checks per iteration: " + checksPerIteration);

        // set initial stepSize such that #checksPerIteration are done (larger means less precise)
        int stepSize = Math.max((int) (numPossibleHeights/checksPerIteration), 1);

        // set current bests with the maximum possible height
        double currentBestHeight = maximumHeight;
        parameters.heightVariant = Util.HeightSupport.FIXED;
        parameters.height = (int) currentBestHeight;
        bestSolution = subSolver.pack(parameters.copy());

        int solves = 0;
        boolean firstIteration = true; // used to determine whether to record to chart or not

        do {
            System.out.println("Stepsize: " + stepSize);
            for (double newHeight = startRange + stepSize; newHeight <= stopRange - stepSize; newHeight += stepSize) {
                Parameters params = parameters.copy();
                params.height = (int) newHeight;
                Solution newSolution = subSolver.pack(params);
                solves++;


                if (newSolution.getArea(true) < bestSolution.getArea(true)) {
                    // update bestSolution
                    currentBestHeight = (int) newHeight;
                    bestSolution = newSolution;
                }

            }

            // update ranges around the best found value
            startRange = (int) Math.max(minimumHeight, currentBestHeight - stepSize);
            stopRange = (int) Math.min(maximumHeight, currentBestHeight + stepSize);

            // update stepSize
            stepSize = Math.max((int) ((stopRange - startRange)/checksPerIteration), 1);
        } while (stepSize > 1);
        System.out.println("Solves: " + solves);

        return bestSolution;
    }
}
