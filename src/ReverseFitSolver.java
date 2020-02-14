import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Solver algorithm for 2d strip packing without rotations (for now).
 * See https://link.springer.com/chapter/10.1007/BFb0049416 for the algorithm
 */
public class ReverseFitSolver extends AbstractSolver {

    /**
     * Find the value for the parameters without doing any other output.
     *
     * @param parameters The parameters to be used by the solver.
     * @pre {@code parameters.heightVariant == HeightVariant.FIXED }
     * @return Returns the optimal area found by this solver.
     */
    @Override
    Solution optimal(Parameters parameters) throws IllegalArgumentException {
        // arrayList with rectangles in final position
        ArrayList<Rectangle> placedRectangles = new ArrayList<>();

        // put in all rectangles with 2* height > parameters.height on the ground

        // split in lists and keep track of largest width of smallHeightRectangles
        ArrayList<Rectangle> largeHeightRectangles = new ArrayList<>();
        ArrayList<Rectangle> smallHeightRectangles = new ArrayList<>();

        for (Rectangle rectangle: parameters.rectangles) {
            if (2 * rectangle.height > parameters.height) {
                largeHeightRectangles.add(rectangle);
            } else {
                smallHeightRectangles.add(rectangle);
            }
        }

        // STEP 1

        int placeX = 0;  // x value where the next largeHeightRectangle is going to be placed
        for (Rectangle rectangle : largeHeightRectangles) {
            rectangle.setLocation(placeX, 0); // place rectangle on bottom next to previous rectangle
            placedRectangles.add(rectangle);
            placeX += rectangle.width;  // update placeX to next to rectangle
        }
        // Notice placeX equivalent to H_0 in article

        // STEP 2

        // sort smallHeightRectangles based on decreasing width
        smallHeightRectangles.sort(Comparator.comparing(Rectangle::getWidth));
        // get largest width
        int largestWidthSmallHeight = smallHeightRectangles.get(0).height; // equivalent to h_{max} in article

        // STEP 3

        // put rectangles from top to bottom after placeX until it is too high to fit, then go to the right
        int heightStackedSmallRectangles = 0;
        for (Rectangle rectangle : smallHeightRectangles) {
            if (heightStackedSmallRectangles + rectangle.getHeight() > parameters.height) {
                break;
            }
            rectangle.setLocation(placeX, heightStackedSmallRectangles);
            placedRectangles.add(rectangle);
            smallHeightRectangles.remove(rectangle);
            // update heigthtStackedSmallRectangles;
            heightStackedSmallRectangles += rectangle.height;
        }

        // STEP 4
        int largestWidthRemaining = Collections.max(
                smallHeightRectangles,
                Comparator.comparing(Rectangle::getWidth)
        ).width;

        int topAlignHeight = largestWidthRemaining + largestWidthSmallHeight + placeX;
        int currentHeight = parameters.height; // top of where to place new rectangles
        for (Rectangle rectangle : smallHeightRectangles) {
            currentHeight -= rectangle.getHeight();
            if (currentHeight  < 0) {
                // TODO
                break;
            }
            rectangle.setLocation(topAlignHeight - (int) rectangle.getWidth(), currentHeight);
            smallHeightRectangles.remove(rectangle);
            placedRectangles.add(rectangle);
        }


        return new Solution(0, 0, parameters, this);
    }

}
