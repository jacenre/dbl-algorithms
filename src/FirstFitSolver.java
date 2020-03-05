import java.util.ArrayList;

/**
 * Solver algorithm using the First Fit Heuristic where height is fixed.
 * TODO Implement heightVariant and rotationVariant
 */
public class FirstFitSolver extends AbstractSolver {


    public Util.HeightSupport[] heightSupport = new Util.HeightSupport[]{
            Util.HeightSupport.FIXED, Util.HeightSupport.FREE};

    boolean animate = true;

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
        parameters.rectangles.sort((o1, o2) -> (o2.height) - (o1.height));
        parameters.rectangles.sort((o1, o2) -> (o2.width) - (o1.width));

        ArrayList<Box> boxes = new ArrayList<>();

        for (Rectangle rectangle :
                parameters.rectangles) {
            if (animate) Util.animate(parameters, this);
            // First rectangle always fits
            if (boxes.size() == 0) {
                rectangle.x = 0;
                rectangle.y = 0;

                Box newBox = new Box(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                newBox.add(rectangle);

                boxes.add(newBox);
            } else {
                // If the rectangle doesn't fit we create a new box.
                if (!fitRectangle(boxes, rectangle, parameters.height)) {

                    int maxX = 0;
                    for (Box box : boxes) {
                        if (box.y == 0) {
                            maxX = Math.max(box.x + box.width, maxX);
                        }
                    }

                    rectangle.x = maxX;
                    rectangle.y = 0;

                    Box newBox = new Box(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                    newBox.add(rectangle);

                    boxes.add(newBox);
                }
            }
            rectangle.place(true);
        }

        return new Solution(parameters, this);
    }

    /**
     * Tries and fit the rectangle in one of the bins
     *
     * @return {@code true} if it fits in any of the bins, else {@code false}
     */
    private boolean fitRectangle(ArrayList<Box> boxes, Rectangle rectangle, int height) {
        for (Box box :
                boxes) {
            if (rectangle.height + box.height + box.y <= height && rectangle.width <= box.width) {
                rectangle.x = box.x;
                rectangle.y = box.y + box.height;
                box.add(rectangle);

                if (box.rectangles.size() > 1) {
                    Rectangle previousRect = box.rectangles.get(box.rectangles.size() - 2);
                    int boundX = previousRect.width - rectangle.width;

                    Box recursiveBox = new Box(rectangle.x + rectangle.width, rectangle.y, boundX, 0);
                    boxes.add(recursiveBox);
                }

                return true;
            }
        }
        return false;
    }

    private class Box {

        // All the Rectangles in this box.
        ArrayList<Rectangle> rectangles = new ArrayList<>();

        // Top left coordinates of the box.
        int x;
        int y;

        // Size of the Box.
        int width;
        int height;

        Box(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void add(Rectangle rect) {
            rectangles.add(rect);
            this.height = (rect.y + rect.height > this.y + this.height) ? rect.y + rect.height - this.y : this.height;
            this.width = (rect.x + rect.width > this.x + this.width) ? rect.x + rect.width - this.x : this.width;
        }

    }

}