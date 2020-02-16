import java.util.ArrayList;

/**
 * Solver algorithm using the First Fit Heuristic where height is fixed.
 * TODO Implement heightVariant and rotationVariant
 */
public class FirstFitSolver extends AbstractSolver {

    public Util.HeightSupport[] heightSupport = new Util.HeightSupport[]{
            Util.HeightSupport.FIXED, Util.HeightSupport.FREE};

    /**
     * Find the optimal value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the optimal area found by this solver.
     */
    @Override
    Solution optimal(Parameters parameters) {
        // Greedy choice, rotate every rectangle such that it is wider than that it is high.
        if (parameters.rotationVariant) {
            for (Rectangle rectangle :
                    parameters.rectangles) {
                if (rectangle.height > rectangle.width) {
                    rectangle.rotate();
                }
            }
        }

        // Sort the array from large to small
        parameters.rectangles.sort((o1, o2) -> (o2.width) - (o1.width));

        // int[0] is the height of the bin, int[1] is the width, int[2] is x.
        // TODO: Rename bins into boxes, and create a box class
        ArrayList<int[]> bins = new ArrayList<>();

        for (Rectangle rectangle :
                parameters.rectangles) {
            // First rectangle always fits
            if (bins.size() == 0) {
                rectangle.x = 0;
                rectangle.y = 0;
                bins.add(new int[]{rectangle.height, rectangle.width, 0});
            } else {
                // If the rectangle doesn't fit we create a new bin.
                if (!fitRectangle(bins, rectangle, parameters.height)) {
                    rectangle.x = bins.get(bins.size() - 1)[1];
                    rectangle.y = 0;
                    int[] lastBin = bins.get(bins.size() - 1);
                    bins.add(new int[]{rectangle.height, rectangle.x + rectangle.width, lastBin[1]});
                }
            }
        }

        return new Solution(parameters, this);
    }

    /**
     * Tries and fit the rectangle in one of the bins
     * @return {@code true} if it fits in any of the bins, else {@code false}
     */
    private boolean fitRectangle(ArrayList<int[]> bins, Rectangle rectangle, int height) {
        for (int[] bin :
                bins) {
            if (rectangle.height + bin[0] <= height) {
                rectangle.x = bin[2];
                rectangle.y = bin[0];

                bin[0] += rectangle.height;
                return true;
            }
        }
        return false;
    }
}