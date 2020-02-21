import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
            if (newSolution.getArea() < bestSolution.getArea()) {
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
        }

        for (int i = minHeight; i <= maxHeight; i++) {
            heights.add(i);
        }

        return heights;
    }

}