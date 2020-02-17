
import java.util.ArrayList;

/**
 * Global Util class for commonly used function and constants.
 */
public class Util {

    /**
     * Used for checking height variants.
     */
    public enum HeightSupport {
        FIXED,
        FREE
    }

    /**
     * Creates a deep copy of a {@link Rectangle} ArrayList with the same {@link Rectangle#getId()} for each rectangle.
     * @param rects Input array to copy
     * @return A deep copy ArrayList.
     */
    public static ArrayList<Rectangle> cloneRectangleState(ArrayList<Rectangle> rects) {
        ArrayList<Rectangle> rectangles = new ArrayList<>();
        for (Rectangle rect:
                rects) {
            rectangles.add(new Rectangle(rect));
        }
        return rectangles;
    }

    /**
     * Used for animating the current parameters.
     * @param parameters Parameters to animate.
     * @param solver Solver that called the animation.
     * TODO COMMENT OUT BEFORE HANDING IN.
     */
    public static void animate(Parameters parameters, AbstractSolver solver) {
        if (Animator.animator == null) {
            Animator.main(new String[]{});
        } else {
            Animator.animator.draw();
            Animator.animator.drawParameter(parameters, solver);
        }
    }

    public static void animate() {
        if (Animator.animator == null) {
            Animator.main(new String[]{});
        } else {
            Animator.animator.draw();
        }
    }

}