/**
 * Intent: the solution object should contain all the information about the input and output
 * in such a way that only a solution object is needed to verify its correctness
 */
public class Solution {

    /**
     * The parameters that were used during solving.
     */
    public Parameters parameters;

    /**
     * The area the solution takes up
     */
    public int getArea() {
        return this.getWidth() * this.getHeight();
    }

    /**
     * Height of the total bin area
     */
    public int getHeight() {
        if (parameters.heightVariant == HeightSupport.FIXED) {
            return parameters.height;
        }

        // If height is fixed return the fix height
        int maxHeight = 0;

        for (Rectangle rectangle :
                this.parameters.rectangles) {
            if (rectangle.y + rectangle.height > maxHeight) maxHeight = rectangle.y + rectangle.height;
        }

        return maxHeight;
    };

    /**
     * Width of the total bin area
     */
    public int getWidth() {
        int maxWidth = 0;

        for (Rectangle rectangle :
                this.parameters.rectangles) {
            if (rectangle.x + rectangle.width > maxWidth) maxWidth = rectangle.x + rectangle.width;
        }

        return maxWidth;
    }

    /**
     * Create a solution object without knowing the solution.
     *
     * @param parameters The parameters used for solving.
     */
    public Solution(Parameters parameters) {
        this.parameters = parameters;
    }

    /**
     * Create a Solution object when you know the width and height of the solution.
     *
     * @param parameters The parameters used for solving.
     */
    public Solution(Parameters parameters, AbstractSolver solvedBy) {
        this.parameters = parameters;
        this.solvedBy = solvedBy;
    }

    /**
     * Debugging information about which solver solved this.
     */
    public AbstractSolver solvedBy;

    /**
     * The sum of all rectangles which must always be as good or better than the found optimal.
     *
     * @return The minimum area representing the sum of all rectangles.
     */
    public int getMinimumArea() {
        int minimumArea = 0;
        for (Rectangle rectangle :
                parameters.rectangles) {
            minimumArea += (rectangle.height * rectangle.width);
        }
        return minimumArea;
    }

    /**
     * Returns the rate of the solution
     * @return solution / minimum
     */
    public double getRate() {
        return (double) this.getArea() / (double) this.getMinimumArea();
    }

    /**
     * For debugging information in test cases.
     * @return String representation of this object.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Solution found by ").append(solvedBy.getClass().getSimpleName()).append("\n \n");
        stringBuilder.append("Amount of rectangles ").append(this.parameters.rectangles.size()).append("\n");
        stringBuilder.append("Minimum area is ").append(this.getMinimumArea()).append("\n");
        stringBuilder.append("Found area is ").append(this.getArea()).append("\n");
        stringBuilder.append("OPT rate of ").append((double) this.getArea() / (double) this.getMinimumArea()).append("\n");
        return stringBuilder.toString();
    }

    /**
     * Create a deep copy of this Solution object
     * @return Deep copy solution object.
     */
    public Solution copy() {
        Solution solution = new Solution(this.parameters.copy());
        solution.solvedBy = this.solvedBy;
        return solution;
    }
}
