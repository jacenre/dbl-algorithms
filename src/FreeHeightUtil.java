public class FreeHeightUtil {

    AbstractSolver subsolver;

    public FreeHeightUtil(AbstractSolver subsolver) {
        this.subsolver = subsolver;
    }

    Solution getSolution(Parameters parameters) {
        Util.animate(parameters, subsolver);
        Solution bestSolution;

        if (parameters.rectangles.size() > 100) {
            bestSolution = localMinimaFinder(parameters, 0.01);
        } else {
            bestSolution = localMinimaFinder(parameters, 1);
        }

        Util.animate(parameters, subsolver);

        bestSolution.parameters.heightVariant = Util.HeightSupport.FREE;
        bestSolution.solvedBy = subsolver;
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
        parameters.height = (int) stopRange;
        Solution bestSolution = subsolver.pack(parameters.copy());

        boolean firstIteration = true;
        int[] chartYData = new int[(int)((stopRange - startRange)/searchSize) + 1];
        int[] chartXData = new int[(int)((stopRange - startRange)/searchSize) + 1];
        int iter = 0;

        while (stopRange - startRange > 1) {
            for (double i = startRange; i <= stopRange; i += searchSize) {

                Parameters params = parameters.copy();
                params.height = (int) i;

                Solution newSolution = subsolver.pack(params);

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
