import java.util.ArrayList;


/**
 * Solver algorithm for 2d strip packing without rotations (for now).
 */
public class ReverseFitSolver extends AbstractSolver {

    /**
     * Find the value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @pre {@code parameters.heightVariant.equals("fixed") }
     * @return Returns the optimal area found by this solver.
     */
    @Override
    Solution optimal(Parameters parameters) throws IllegalArgumentException {
        if (!parameters.heightVariant.equals("fixed"))
            throw new IllegalArgumentException("ReverseFitSolver only solves with fixed height.");

        // put in all rectangles with 2* height > parameters.height on the ground

        // split in lists
        ArrayList<Rectangle> largeHeightRectangles = new ArrayList<>();
        ArrayList<Rectangle> smallHeightRectangles = new ArrayList<>();

        for (Rectangle rectangle: parameters.rectangles) {
            if (2 * rectangle.height > parameters.height) {
                largeHeightRectangles.add(rectangle);
            } else {
                smallHeightRectangles.add(rectangle);
            }
        }
        int placeX = 0;  // x value where the next largeHeightRectangle is going to be placed
        for (Rectangle rectangle: largeHeightRectangles) {
            rectangle.setLocation(placeX, 0);
            placeX += rectangle.width;  // update placeX to next to rectangle
        }


        return new Solution(0, 0, parameters);
    }

}
