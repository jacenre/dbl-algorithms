package jacenre.dbla;
import java.util.ArrayList;

/**
 * Solver algorithm using the First Fit Heuristic where height is fixed.
 * TODO Implement rotationVariant
 */
public class FirstFitSolver extends AbstractSolver {

    boolean animate = true;


    public FirstFitSolver(boolean allowInputSorting) {
        super(allowInputSorting);
    }
    public FirstFitSolver() {
        super();
    }

    /**
     * Find the pack value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the pack area found by this solver.
     */
    @Override
    Solution pack(Parameters parameters) {
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
        parameters.rectangles.sort((o1, o2) -> o2.height - o1.height);
        parameters.rectangles.sort((o1, o2) -> o2.width - o1.width);

        ArrayList<Box> boxes = new ArrayList<>();

        for (Rectangle rectangle :
                parameters.rectangles) {
            // First rectangle always fits
            if (boxes.size() == 0) {
                rectangle.x = 0;
                rectangle.y = 0;

                Box newBox = new Box(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                newBox.add(rectangle);

                boxes.add(newBox);
            } else // If the rectangle doesn't fit we create a new box.
			if (!fitRectangle(boxes, rectangle, parameters.height)) {

			    long maxX = 0;
			    for (Box box : boxes) {
			        if (box.y == 0) {
			            maxX = Math.max(box.x + box.width, maxX);
			        }
			    }

			    rectangle.x = (int) maxX;
			    rectangle.y = 0;

			    Box newBox = new Box(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
			    newBox.add(rectangle);

			    boxes.add(newBox);
			}
            rectangle.place(true);
            if (animate) {
				Util.animate(parameters, this);
			}
        }

        return new Solution(parameters, this);
    }

    /**
     * Tries and fit the rectangle in one of the boxes
     *
     * @return {@code true} if it fits in any of the boxes, else {@code false}
     */
    private boolean fitRectangle(ArrayList<Box> boxes, Rectangle rectangle, long height) {
        for (Box box : boxes) {
            // If adding the box respects the height limit and isn't to wide...
            if (rectangle.height + box.height + box.y <= height && rectangle.width <= box.width) {
                rectangle.x = (int) box.x;
                rectangle.y = (int) (box.y + box.height);
                box.add(rectangle);

                // Create a new box to the right of the rectangle
                if (box.rectangles.size() > 1) {
                    Rectangle previousRect = box.rectangles.get(box.rectangles.size() - 2);
                    long boundX = previousRect.width - rectangle.width;

                    Box recursiveBox = new Box(rectangle.x + rectangle.width, rectangle.y, boundX, 0);
                    boxes.add(recursiveBox);
                }

                // Success
                return true;
            }
        }
        // Failure
        return false;
    }

    // Boxes in which we store rectangles
    private static class Box {

        // All the Rectangles in this box.
        ArrayList<Rectangle> rectangles = new ArrayList<>();

        // Top left coordinates of the box.
        long x;
        long y;

        // Size of the Box.
        long width;
        long height;

        // The width and height are soft wrap, meaning that adding a bigger rectangle overwrites these.
        Box(long x, long y, long width, long height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        // Add a new rectangle to this box.
        public void add(Rectangle rect) {
            rectangles.add(rect);
            this.height = rect.y + rect.height > this.y + this.height ? rect.y + rect.height - this.y : this.height;
            this.width = rect.x + rect.width > this.x + this.width ? rect.x + rect.width - this.x : this.width;
        }

    }

}