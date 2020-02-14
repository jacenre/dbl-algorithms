import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Binary search on the fixed results of First Fit solver
 */
public class FreeFirstFitSolver extends AbstractSolver {

    @Override
    Set<HeightSupport> getHeightSupport() {
        return new HashSet<>(Arrays.asList(HeightSupport.FREE));
    }

    // ArrayList to binary search on.
    ArrayList<Integer> heights = new ArrayList<>();

    FirstFitSolver firstFitSolver = new FirstFitSolver();

    Solution bestSolution;

    /**
     * Find the optimal value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the optimal area found by this solver.
     */
    @Override
    Solution optimal(Parameters parameters) {
        if (parameters.rectangles.size() > 10000) {
            throw new IllegalArgumentException();
        }

        // Get all the possible heights
        heights = getHeights(parameters);

        // Set the heightVariant for the first fit solver
        parameters.heightVariant = HeightSupport.FIXED;

        for (int height :
                heights) {
            parameters.height = height;
            Solution newSolution = firstFitSolver.optimal(parameters);
            if (newSolution.getArea() < bestSolution.getArea()) {
                bestSolution.parameters = parameters;
                bestSolution.setHeight(newSolution.height);
                bestSolution.setWidth(newSolution.width);
            }
        }

        bestSolution.parameters.heightVariant = HeightSupport.FREE;
        return bestSolution;
    }

    /**
     * Creates an ArrayList of all the y's of the first fit solver without height limit.
     * @param parameter The parameters used for the solve
     * @return ArrayList containing all the y's.
     */
    ArrayList<Integer> getHeights(Parameters parameters) {
        ArrayList<Integer> heights = new ArrayList<>();
        // Set to fixed and give it to the first fit solver.
        parameters.heightVariant = HeightSupport.FIXED;
        Solution solution = firstFitSolver.solve(parameters);

        // Create new best solution
        bestSolution = new Solution(solution.width, solution.height, parameters, this);

        for (Rectangle rectangle :
                solution.parameters.rectangles) {
            heights.add(rectangle.y + rectangle.height);
        }

        return heights;
    }

}