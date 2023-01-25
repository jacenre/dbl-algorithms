package jacenre.dbla;
import java.util.ArrayList;

import net.jafama.FastMath;

/**
 * Util that allows any {@code Util.HeightSupport.FIXED} to be turned into a {@code Util.HeightSupport.FREE} solver
 * using local minima finder.
 */
public class FreeHeightUtil {

    /**
     * The AbstractSolver used during {@link #localMinimaFinder(Parameters, int)}, by default {@link FirstFitSolver}.
     */
    private AbstractSolver subSolver;

    /**
     * Constructor that sets the {@code subSolver}
     *
     * @param subSolver the AbstractSolver to use
     * @see #localMinimaFinder(Parameters, int)
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

        // Calculate the total number of possible heights
        final int minimumHeight = Util.largestRect(parameters);
        final int maximumHeight = Util.sumHeight(parameters);
        int numPossibleHeights = maximumHeight - minimumHeight;

        // Find how much time Solve takes
        long startTime = System.nanoTime();
        double currentBestHeight = maximumHeight / 2;
        // perform a solve
        parameters.heightVariant = Util.HeightSupport.FIXED;
        parameters.height = (int) currentBestHeight;
        Solution bestSolution = subSolver.pack(parameters.copy());
        long endTime = System.nanoTime();

        long duration = Math.max((endTime - startTime) / 1000000, 1); // duration of subSolver.pack or 1 if too fast

        // Time allowed in milliseconds
        final int ALLOWED_TIME = 30000; // 25 seconds which leaves 5 seconds for other stuff
        int numChecks = (int) (ALLOWED_TIME / duration); // amount of checks that can be done
        if (Util.debug) {
			System.out.println("numChecks: " + numChecks);
		}
        // find best heights
        if (numChecks >= numPossibleHeights) { // if more checks can be done than the max needed
            bestSolution = tryAllHeightsFinder(parameters);
        } else {
            bestSolution = localMinimaFinder(parameters, numChecks);
        }

        // Set the amount of checks to be done
        Util.animate(parameters, subSolver);

        bestSolution.parameters.freeHeightUtil = false; // change as if not processed by freeHeightUtil
        bestSolution.parameters.heightVariant = Util.HeightSupport.FREE;
        return bestSolution;
    }

    /**
     * Return best solution dependent on the height.
     *
     * @param parameters of the problem
     * @param numChecks  number of checks to do at most
     * @return best Solution found
     */
    Solution localMinimaFinder(Parameters parameters, int numChecks) {

        // Starting conditions
        final long minimumHeight = Util.largestRect(parameters);
        final long maximumHeight = Util.sumHeight(parameters);

        // range over which to check for best solution (will get smaller each recursion)
        long startRange = minimumHeight;
        long stopRange = maximumHeight;

        // number of possible heights that could be used to solve
        long numPossibleHeights = maximumHeight - minimumHeight;

        // smallest stepSize to reach before stopping (smaller is more precise)
        double stepSizePrecision = 1;

        // For the math behind this, refer to Tristan Trouwen (or maybe the report in a later stage)
        // for simplification of expression of checksPerIteration
        double L1 = FastMath.log((float) stepSizePrecision / numPossibleHeights);

        double numRecursions, checksPerIteration;
        // check if not legal
        if (2 * L1 / numChecks < -1 / FastMath.exp(1) || 2 * L1 / numChecks >= 0) {
            // numChecks = - (int) (FastMath.log((float)1/numPossibleHeights)*2*FastMath.exp(1))+2; // amount of checks that should be needed
            numRecursions = 1; // no recursion
            stepSizePrecision = (float) numPossibleHeights / numChecks;
            checksPerIteration = numChecks;

        } else { // legal
            // approximate number of recursions that will be made
            numRecursions = L1 / MathUtil.LambertMinusOne(2 * L1 / numChecks);

            checksPerIteration = numChecks * MathUtil.LambertMinusOne(2 * L1 / numChecks) / L1;
        }

        if (Util.debug) {
            System.out.println("Possible heights to try (H): " + numPossibleHeights);
            System.out.println("Approximate number of recursions (M): " + numRecursions);
            System.out.println("Checks per iteration (m): " + checksPerIteration);
            System.out.println("M*m: " + checksPerIteration * numRecursions);
            System.out.println("StepSize precision: " + stepSizePrecision);
        }

        // set current bests with the maximum possible height
        double currentBestHeight = maximumHeight / 2;
        parameters.heightVariant = Util.HeightSupport.FIXED;
        parameters.height = (int) currentBestHeight;
        Solution bestSolution = subSolver.pack(parameters.copy());

        int solves = 0; // used to record the number of solves for debug purposes
        boolean firstIteration = true; // used to determine whether to record to chart or not

        // stepSize such that #checksPerIteration are done (larger means less precise) is made smaller each iteration
        int stepSize;
        // record heights that were tried already
        ArrayList<Double> triedHeights = new ArrayList<>();
        do {
            // update stepSize
            stepSize = Math.max((int) ((stopRange - startRange) / checksPerIteration), 1);
            if (Util.debug) {
				System.out.println("Stepsize: " + stepSize);
			}

            for (double newHeight = startRange + stepSize; newHeight <= stopRange - stepSize; newHeight += stepSize) {
                if (triedHeights.contains(newHeight))
				 {
					continue; // skip if already tried
				}
                Parameters params = parameters.copy();
                params.height = (int) newHeight;
                Solution newSolution = subSolver.pack(params);
                solves++;

                if (newSolution == null) {
					continue;
				}

                if (newSolution.getRate() == 1.0d) {
                    return newSolution;
                }

                if (newSolution.isBetter(bestSolution)) {
                    // update bestSolution
                    currentBestHeight = (int) newHeight;
                    bestSolution = newSolution;
                }
                triedHeights.add(newHeight);
            }

            // update ranges around the best found value
            startRange = (int) Math.max(minimumHeight, currentBestHeight - stepSize);
            stopRange = (int) Math.min(maximumHeight, currentBestHeight + stepSize);
        } while (stepSize > stepSizePrecision && numRecursions > 1);

        if (Util.debug) {
			System.out.println("Solves: " + solves);
		}
        return bestSolution;
    }

    /**
     * Tries to find a solution with all height parameters and returns best solution found.
     *
     * @param parameters of the problem
     * @return best Solution found
     */
    Solution tryAllHeightsFinder(Parameters parameters) {
        // Starting conditions
        final int minimumHeight = Util.largestRect(parameters);
        final int maximumHeight = Util.sumHeight(parameters);

        Solution bestSolution = null; // holds best solution found so far

        for (int newHeight = minimumHeight; newHeight <= maximumHeight; newHeight++) {
            Parameters params = parameters.copy();
            params.height = newHeight;
            Solution newSolution = subSolver.pack(params);

            if (newSolution == null)
			 {
				continue; // not a good solution, skip
			}
            if (newSolution.getRate() == 1.0d) {
                return newSolution;
            }

            if (newSolution.isBetter(bestSolution)) {
                // update bestSolution
                bestSolution = newSolution;
            }
        }
        return bestSolution;
    }
}
