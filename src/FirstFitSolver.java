import java.util.ArrayList;

/**
 * Solver algorithm using the First Fit Heuristic where height is fixed.
 * TODO Implement heightVariant and rotationVariant
 */
public class FirstFitSolver extends AbstractSolver {

    public Util.HeightSupport[] heightSupport = new Util.HeightSupport[]{
            Util.HeightSupport.FIXED, Util.HeightSupport.FREE};

    /**
     * Find the optimal value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @return Returns the optimal area found by this solver.
     */
    @Override
    Solution optimal(Parameters parameters) {
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
        parameters.rectangles.sort((o1, o2) -> (o2.width) - (o1.width));

        ArrayList<Box> boxes = new ArrayList<>();

        for (Rectangle rectangle :
                parameters.rectangles) {
            Util.animate(parameters, this);
            // First rectangle always fits
            if (boxes.size() == 0) {
                rectangle.x = 0;
                rectangle.y = 0;

                Box newBox = new Box(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                newBox.add(rectangle);

                boxes.add(newBox);
            } else {
                // If the rectangle doesn't fit we create a new bin.
                if (!fitRectangle(boxes, rectangle, parameters.height)) {
                    Box latest = boxes.get(boxes.size() - 1);
                    rectangle.x = latest.x + latest.width;
                    rectangle.y = latest.y;

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
            if (rectangle.height + box.height + box.y <= height) {
                rectangle.x = box.x;
                rectangle.y = box.y + box.height;
                box.add(rectangle);
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