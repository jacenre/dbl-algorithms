import java.util.ArrayList;

/**
 * Run multiple solvers on the given parameters and return the best solution.
 */
public class CompoundSolver extends AbstractSolver {

    public ArrayList<AbstractSolver> getSolvers() {
        return solvers;
    }

    public void setSolvers(ArrayList<AbstractSolver> solvers) {
        this.solvers = solvers;
    }

    /**
     * ArrayList containing all the solvers used.
     */
    private ArrayList<AbstractSolver> solvers = new ArrayList<>();

    /**
     * Add a solver to the CompoundSolver.
     *
     * @return this CompoundSolver
     */
    public CompoundSolver addSolver(AbstractSolver solver) {
        this.solvers.add(solver);
        return this;
    }

    /**
     * Returns a {@code Solution} for the given {@code Parameters}
     * <p>
     * Contains the template code for most solvers. By default it will rotate any rectangle
     * that are to high to fit in a fixed box, if applicable. It will also check if the {@code Parameter }
     * heightVariant and the supported height variants of this {@code Solver} match.
     * </p>
     *
     * @param parameters the parameters for which to getSolution
     * @return a {@code Solution} object containing the results
     * @throws IllegalArgumentException if the Solver cannot getSolution the given parameters
     */
    public Solution getSolution(Parameters parameters) throws IllegalArgumentException {
        if (!getHeightSupport().contains(parameters.heightVariant)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    " does not support " + parameters.heightVariant);
        }

        // General rule, if rotating make sure that every rectangle fits.
        if (parameters.rotationVariant) {
            for (Rectangle rectangle :
                    parameters.rectangles) {
                if (rectangle.height > parameters.height) {
                    rectangle.rotate();
                }
            }
        }

        Solution solution; // Solution

        // Create a new solution for this getSolution.
        if (parameters.heightVariant.equals(Util.HeightSupport.FIXED)) {
            solution = this.pack(parameters);
        } else {
            Parameters initialParameters = parameters.copy();
            Solution bestSolution = null;
            // Try and getSolution it using all the solvers in the array
            for (AbstractSolver solver :
                    this.getSolvers()) {
                try {
                    Solution newSolution = new FreeHeightUtil(solver).pack(initialParameters.copy());
                    // If we found a better solution.
                    if (bestSolution == null || newSolution.getArea() < bestSolution.getArea()) {
                        bestSolution = newSolution.copy();
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
            solution = bestSolution;
        }

        // report(solution);
        return solution;
    }

    /**
     * Returns the name of the subSolver that solved it.
     * @return the simple class name of the sub solver
     */
    @Override
    String getName() {
        return this.bestSolution.solvedBy.getName();
    }

    /**
     * Solves the given {@code parameters} using every {@code Solver} in {@code solvers}, returns the {@code Solution}
     * object associated with the best score found.
     * <p>
     * Ignores any thrown {@code IllegalArgumentException}. Deep copies the {@code parameters} before giving
     * it to a solver.
     * </p>
     *
     * @param parameters the {@code Parameters} to be used by the solver
     * @return a {@link Solution} object associated with the smallest area
     */
    @Override
    public Solution pack(Parameters parameters) {
        Parameters initialParameters = parameters.copy();
        Solution bestSolution = null;
        // Try and getSolution it using all the solvers in the array
        for (AbstractSolver solver :
                solvers) {
            try {
                Solution solution = solver.pack(initialParameters.copy());
                // If we found a better solution.
                if (bestSolution == null || solution.getArea() < bestSolution.getArea()) {
                    bestSolution = solution.copy();
                }
            } catch (Exception e) {
                // ignore
            }
        }
        return bestSolution;
    }

}
