import java.util.*;

/**
 * Free height version of the FirstFitSolver.
 */
public class FreeFirstFitSolver extends AbstractSolver {

    @Override
    Set<Util.HeightSupport> getHeightSupport() {
        return new HashSet<>(Arrays.asList(Util.HeightSupport.FREE));
    }

    // ArrayList to binary search on.
    ArrayList<Integer> heights = new ArrayList<>();

    FirstFitSolver firstFitSolver = new FirstFitSolver();

    Solution bestSolution = null;

    /**
     * Find the pack value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the pack area found by this solver.
     */
    @Override
    Solution pack(Parameters parameters) {
        if (parameters.rectangles.size() > 5000) {
            throw new IllegalArgumentException();
        }

        firstFitSolver.animate = false;

        heights = getHeights(parameters);

        Util.animate(parameters, this);

        // Set the heightVariant for the first fit solver
        parameters.heightVariant = Util.HeightSupport.FIXED;

        for (int height :
                heights) {
            Parameters newParameters = parameters.copy();
            newParameters.height = height;

            Solution newSolution = firstFitSolver.pack(newParameters.copy());
            Util.animate(newSolution.parameters, this);

            if (bestSolution == null) {
                bestSolution = newSolution.copy();
            }

            int maxHeight = 0;

            for (Rectangle rectangle :
                    newSolution.parameters.rectangles) {
                if (rectangle.y + rectangle.height > maxHeight) maxHeight = rectangle.y + rectangle.height;
            }

            if (maxHeight * newSolution.getWidth() < bestSolution.getArea()) {
                bestSolution = newSolution.copy();
            }
        }

        Util.animate(parameters, this);

        bestSolution.parameters.heightVariant = Util.HeightSupport.FREE;
        bestSolution.solvedBy = this;
        return bestSolution;
    }

    /**
     * Creates an ArrayList of all the y's of the first fit solver without height limit.
     * @param parameters The parameters used for the getSolution
     * @return ArrayList containing all the y's.
     */
    ArrayList<Integer> getHeights(Parameters parameters) {
        ArrayList<Integer> heights = new ArrayList<>();

        int minHeight = 0;
        int maxHeight = 0;

        for (Rectangle rectangle : parameters.rectangles) {
            maxHeight += rectangle.height;
            minHeight = (rectangle.height > minHeight) ? rectangle.height : minHeight;
            heights.add(rectangle.height);
        }

        int[] height = new int[heights.size()];
        for (int i = 0; i < heights.size(); i++) {
            height[i] = heights.get(i);
        }

        isSubsetSum(height, height.length, maxHeight);

        ArrayList<Integer> subsetHeights = new ArrayList<>();
        for (int i = minHeight; i < maxHeight+1; i++) {
            if (subset[i][height.length]) {
                subsetHeights.add(i);
            }
        }

        return subsetHeights;
    }

    boolean subset[][];

    // Returns true if there is a subset of
    // set[] with sum equal to given sum
    boolean isSubsetSum(int set[],
                               int n, int sum)
    {
        // The value of subset[i][j] will be
        // true if there is a subset of
        // set[0..j-1] with sum equal to i
        subset =
                new boolean[sum+1][n+1];

        // If sum is 0, then answer is true
        for (int i = 0; i <= n; i++)
            subset[0][i] = true;

        // If sum is not 0 and set is empty,
        // then answer is false
        for (int i = 1; i <= sum; i++)
            subset[i][0] = false;

        // Fill the subset table in botton
        // up manner
        for (int i = 1; i <= sum; i++)
        {
            for (int j = 1; j <= n; j++)
            {
                subset[i][j] = subset[i][j-1];
                if (i >= set[j-1])
                    subset[i][j] = subset[i][j] ||
                            subset[i - set[j-1]][j-1];
            }
        }

        return subset[sum][n];
    }
}
