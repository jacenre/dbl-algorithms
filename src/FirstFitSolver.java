import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Solver algorithm using the First Fit Heuristic where height is fixed.
 */
public class FirstFitSolver extends AbstractSolver {
    /**
     * Solves for the given parameters
     *
     * @param parameters The parameters to be used by the solver.
     */
    @Override
    void solve(Parameters parameters) {

    }

    /**
     * Find the optimal value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the optimal area found by this solver.
     */
    @Override
    int optimal(Parameters parameters) {
        int size = 0;

        // Sort the array from large to small
        parameters.rectangles.sort(new Comparator<Rectangle>() {
            @Override
            public int compare(Rectangle o1, Rectangle o2) {
                return (o2.width) - (o1.width);
            }
        });

        // int[0] is the y of the bin, int[1] is the width.
        ArrayList<int[]> bins = new ArrayList<>();

        for (Rectangle rectangle :
                parameters.rectangles) {
            // First rectangle always fits
            if (bins.size() == 0) {
                rectangle.x = 0;
                rectangle.y = 0;
                bins.add(new int[]{rectangle.height, rectangle.width});
            } else {
                // If the rectangle doesnt fit we create a new bin.
                if (!fitRectangle(bins, rectangle, parameters.height)) {
                    rectangle.x = bins.get(bins.size() - 1)[1];
                    rectangle.y = 0;
                    bins.add(new int[]{rectangle.height, rectangle.x + rectangle.width});
                }
            }
        }

        // Keeps track of the largest bin
        int largestHeight = 0;

        for (int[] bin :
                bins) {
            if (bin[0] > largestHeight) {
                largestHeight = bin[0];
            }
        }

        size = largestHeight * bins.get(bins.size() - 1)[1];

        return size;
    }

    /**
     * Tries and fit the rectangle in one of the bins
     * @return {@code true} if it fits in any of the bins, else {@code false}
     */
    boolean fitRectangle(ArrayList<int[]> bins, Rectangle rectangle, int height) {
        for (int[] bin :
                bins) {
            if (rectangle.height + bin[0] <= height) {
                rectangle.x = bin[1];
                rectangle.y = bin[0];

                bin[0] += rectangle.height;
                return true;
            }
        }
        return false;
    }
}
