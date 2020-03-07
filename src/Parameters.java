import java.util.ArrayList;

/**
 * Data type to store all the parameters of the problem.
 *
 * @see <a href="https://canvas.tue.nl/files/1978093/download?download_frd=1"> Problem description 2.1 Input Format</a>
 */
public class Parameters {

    /**
     * The height variant of the parameter.
     * <p>
     * Describes the variant that needs to be solved.
     * Where variant {@code \in {free, fixed}}
     * </p>
     *
     * @see Util.HeightSupport
     */
    public Util.HeightSupport heightVariant;

    /**
     * Boolean representing if this Parameters object was parsed by the {@link FreeHeightUtil}.
     * <p>
     *     The reason for this boolean is that when you use the compound solver in the {@code FreeHeightUtil} it will
     *     set the {@link #heightVariant} to {@code HeightSupport.FIXED} and since it only checks the HeightSupport at
     *     the beginning of the {@link FreeHeightUtil#pack(Parameters)} it will send the {@code Parameters} to all the
     *     children of a {@link CompoundSolver} without them being suited for free height solving.
     * </p>
     * <p>
     *     If this boolean is set to {@code True} it means that the {@code heightVariant} should be overruled to FIXED.
     * </p>
     */
    public boolean freeHeightUtil;

    /**
     * The height of the {@link #heightVariant}.
     * <p>
     *     If the input has {@code heightVariant == Util.HeightSupport.FIXED} then this will represent the maximum
     *     height of the solution.
     * </p>
     */
    public Integer height = 0;

    /**
     * The rotation variant of the Parameter.
     * <p>
     *     if {@code this == true} then rotation is enabled.
     * </p>
     */
    public boolean rotationVariant;

    /**
     * The ArrayList containing all the rectangles.
     *
     * @see Rectangle
     */
    public ArrayList<Rectangle> rectangles;

    /**
     * Returns a deep copy of this parameter object.
     *
     * @return a deep copy of parameters.
     */
    public Parameters copy() {
        Parameters parameters = new Parameters();
        parameters.heightVariant = this.heightVariant;
        parameters.height = this.height;
        parameters.rotationVariant = this.rotationVariant;
        parameters.rectangles = Util.cloneRectangleState(rectangles);
        return parameters;
    }

    /**
     * Prints this {@code Solution} object as a string, containing debug information.
     *
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder();
        if (this.heightVariant == Util.HeightSupport.FIXED) {
            toString.append("container height: fixed ").append(this.height);
        } else {
            toString.append("container height: free");
        }
        toString.append("\n");
        toString.append("rotations allowed: ");
        if (this.rotationVariant) {
            toString.append("yes");
        } else {
            toString.append("no");
        }
        toString.append("\n");
        toString.append("number of rectangles: ").append(this.rectangles.size()).append("\n");
        for (Rectangle rectangle : rectangles) {
            toString.append(rectangle.width).append(" ").append(rectangle.height).append(" ");
        }
        return toString.toString();
    }
}
