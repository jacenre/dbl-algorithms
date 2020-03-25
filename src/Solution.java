/**
 * Solution object encapsulating the results of solving a {@link Parameters} object.
 * <p>
 * The Solution object was made to make a clear distinction between the {@code Parameters} representing a solved
 * and an unsolved situation. In case of an unsolved situation there is little use to asking for the Area, thus
 * these function have been added to the {@code Solution} object to make the use cases clear in a natural way.
 * </p>
 */
public class Solution {

    /**
     * The parameters of the {@code Solution}.
     */
    public Parameters parameters;

    /**
     * Data necessary for the charts
     */
    public int[][] chartData = new int[][]{};

    private double score;

    /**
     * Returns the chartData, which is a double int array containing the x and y axis data.
     *
     * @return the chartData needed in viz
     */
    public int[][] getChartData() {
        return chartData;
    }

    /**
     * Returns the area, or score, of the {@code Solution} as an {@code Integer}.
     *
     * @return the Area of the {@code Solution}.
     */
    public long getArea() {
        return this.getWidth() * this.getHeight();
    }

    /**
     * Returns the area, or score, of the {@code Solution} as an {@code Integer}.
     * <p>
     *     Calling {@code getArea(false)} is the same as calling {@link #getArea()}.
     * </p>
     *
     * @param ignoreHeightVariant the Boolean representing if the height variant should be ignored.
     * @return the Area of the {@code Solution}.
     *
     * @see Util.HeightSupport
     */
    public long getArea(boolean ignoreHeightVariant) {
        return this.getWidth() * this.getHeight(ignoreHeightVariant);
    }

    /**
     * Returns the height of the {@code Solution} as an {@code Integer}.
     * <p>
     *     Returns the fixed height,{@link Parameters#height},
     *     if {@code Parameters.heightVariant == Util.HeightSupport.FIXED}
     * </p>
     *
     * @return the height of the {@code Solution}.
     */
    public long getHeight() {
        // If height is fixed return the fix height
        if (parameters.heightVariant == Util.HeightSupport.FIXED) {
            return parameters.height;
        }

        long maxHeight = 0;

        for (Rectangle rectangle :
                this.parameters.rectangles) {
            if (rectangle.y + rectangle.height > maxHeight) maxHeight = rectangle.y + rectangle.height;
        }

        return maxHeight;
    }

    /**
     * Returns the height of the {@code Solution} as an {@code Integer}.
     * <p>
     *     Returns the fixed height,{@link Parameters#height},
     *     if {@code ignoreHeightVariant == false}.
     *     Calling {@link #getHeight()} is the same as calling {@code getHeight(false)}.
     * </p>
     * @param ignoreHeightVariant a boolean representing if the height variant should be ignored or not
     * @return the height of the {@code Solution}
     */
    public long getHeight(boolean ignoreHeightVariant) {
        if (!ignoreHeightVariant) return (long) getHeight();

        long maxHeight = 0;

        for (Rectangle rectangle :
                this.parameters.rectangles) {
            if (rectangle.y + rectangle.height > maxHeight) maxHeight = rectangle.y + rectangle.height;
        }

        return maxHeight;
    }

    /**
     * Returns the width of the {@code Solution} as an {@code Integer}.
     *
     * @return the width of the {@code Solution}.
     */
    public long getWidth() {
        long maxWidth = 0;

        for (Rectangle rectangle :
                this.parameters.rectangles) {
            if (rectangle.x + rectangle.width > maxWidth) maxWidth = rectangle.x + rectangle.width;
        }

        return maxWidth;
    }

    /**
     * Constructs a new {@code Solution} whose {@link #solvedBy} is unknown.
     *
     * @param parameters the {@code Parameters} used for solving
     */
    public Solution(Parameters parameters) {
        this.parameters = parameters;
    }


    /**
     * Constructs a new {@code Solution} whose {@link #solvedBy} is known.
     *
     * @param parameters the {@code Parameters} used for solving
     * @param solvedBy the {@link AbstractSolver} that created this solution
     */
    public Solution(Parameters parameters, AbstractSolver solvedBy) {
        this.parameters = parameters;
        this.solvedBy = solvedBy;
    }

    /**
     * The {@link AbstractSolver} that created this solution.
     */
    public AbstractSolver solvedBy = null;

    /**
     * Returns the sum of all {@code Rectangles} in {@link #parameters}.
     * <p>
     *     If we find that {@link #getArea()} is less than the sum of all rectangles it must mean that there is an
     *     overlap and the solution is invalid.
     * </p>
     *
     * @return the sum of the area of all rectangles
     */
    public long getMinimumArea() {
        long minimumArea = 0L;
        for (Rectangle rectangle :
                parameters.rectangles) {
            minimumArea += (rectangle.height * rectangle.width);
        }
        return minimumArea;
    }

    /**
     * Returns the OPT rate of the solution.
     *
     * @return a double representing the found area divided by the minimum area
     */
    public double getRate() {
        return (double) this.getArea() / (double) this.getMinimumArea();
    }

    /**
     * Set a double calculated by an external fitness function as a score for the solution.
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * Get a double calculated by an external fitness function as a score for the solution.
     */
    public double getScore() {
        return score;
    }

    /**
     * Prints this {@code Solution} object as a string, containing debug information.
     *
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (solvedBy != null) {
            stringBuilder.append("Solution found by ").append(solvedBy.getName());
            stringBuilder.append("\n \n");
        }
        stringBuilder.append("Amount of rectangles ").append(this.parameters.rectangles.size()).append("\n");
        stringBuilder.append("Minimum area is ").append(this.getMinimumArea()).append("\n");
        stringBuilder.append("Found area is ").append(this.getArea()).append("\n");
        stringBuilder.append("OPT rate of ").append((double) this.getArea() / (double) this.getMinimumArea()).append("\n");
        return stringBuilder.toString();
    }

    /**
     * Returns a deep copy of this {@code Solution} object.
     *
     * @return a {@code Solution} object which is a deep copy of {@code this}
     */
    public Solution copy() {
        Solution solution = new Solution(this.parameters.copy());
        solution.solvedBy = this.solvedBy;
        return solution;
    }
}
