import java.util.ArrayList;
import java.util.List;


/**
 * Solver algorithm ReverseFit.
 * Cfr https://link.springer.com/content/pdf/10.1007%2FBFb0049416.pdf
 */
public class ReverseFitSolver extends AbstractSolver {


    /**
     * Solves for parameters.
     *
     * @param parameters The parameters to be used by the solver.
     * @throws IllegalArgumentException if {@code !parameters.heightVariant.equals("fixed") || parameters.height <= 0 }
     * @return Solution object
     */
    Solution pack(Parameters parameters) throws IllegalArgumentException {
        if (parameters.rectangles.size() > 5000) {
            throw new IllegalArgumentException();
        }
        Util.animate(parameters, this);
        /* Commented out the rotating for now, first of all this should only happen if parameters.rotationVariant and
         * this does not seem very helpful anyway
        ArrayList<Rectangle> remainingRectangles = new ArrayList<>();
        for (Rectangle rectangle : parameters.rectangles) {
            if (rectangle.width > parameters.height / 2) {
                rectangle.rotate();
            }
            remainingRectangles.add(rectangle);
        }
         */


        // STEP 1 #####
        ArrayList<Rectangle> firstLargeRectangles = new ArrayList<>();   // Rectangles with height > parameters.height/2
        ArrayList<Rectangle> remainingRectangles = new ArrayList<>(); // Rectangles with height <= parameters.height/2

        int x_0 = 0; // Keeps track of where to place the blocks in the while loop
        // Stack all the rectangles that have height > parameters.height/2 next to each other
        for (Rectangle rectangle : parameters.rectangles) {
            Util.animate();
            if (rectangle.height > parameters.height / 2) {
                rectangle.place(true);
                rectangle.setLocation(x_0, 0);
                firstLargeRectangles.add(rectangle);
                x_0 += rectangle.width;
            } else {
                remainingRectangles.add(rectangle);
            }
        }

        // With low parameters.height, sometimes we are done here already
        if (remainingRectangles.isEmpty()) {
            return new Solution(parameters, this);
        }

        // STEP 2 #####
        // Sort the remaining rectangles based on width
        remainingRectangles.sort((o1, o2) -> (o2.width) - (o1.width));
        int w_max = remainingRectangles.get(0).width; // Widest of the remaining rectangles (h_{max} in article)
        //Pack the rectangles from up to down along x_0
        int y_0 = 0;
        ArrayList<Rectangle> firstRow = new ArrayList<>();

        // STEP 3 #####
        // Filling in first row
        for (Rectangle rectangle : remainingRectangles) {
            Util.animate();
            rectangle.place(true);
            if (rectangle.height + y_0 > parameters.height) {
                break;
            }
            rectangle.setLocation(x_0, y_0);
            y_0 += rectangle.height;
            firstRow.add(rectangle);
        }
        remainingRectangles.removeAll(firstRow);

        // Either we are done because all the rectangles have been placed, or we need to start with the reverse fit
        if (remainingRectangles.isEmpty()) {
            return new Solution(parameters, this);
        }

        // STEP 4 #####
        int d_1 = remainingRectangles.get(0).width; // width of the widest remaining rectangles

        ArrayList<Rectangle> reverseRow = new ArrayList<>();
        int y_0_reverse = parameters.height;
        for (Rectangle rectangle : remainingRectangles) {
            Util.animate();
            if (y_0_reverse < parameters.height / 2)
                break;  // If the reverse row is this far up, stop
            if (rectangle.height + y_0 < parameters.height) {    // Smaller rectangles might still fit in the first row
                rectangle.setLocation(x_0, y_0);
                y_0 += rectangle.height;
                firstRow.add(rectangle);
            } else { // If it's too big for the first row, then reverse row
                y_0_reverse -= rectangle.height;
                rectangle.setLocation(x_0 + w_max + d_1 - rectangle.width, y_0_reverse);
                reverseRow.add(rectangle);
            }
        }
        remainingRectangles.removeAll(firstRow);
        remainingRectangles.removeAll(reverseRow);


        // Move all the rectangles from the right row to the left until any of them touch
        int moved = 0; // equivalent to e_1 in paper
        while (getTouchingLine(firstRow, reverseRow).length == 0) { // not touching
            moved++;
            for (Rectangle rectangle : reverseRow) {
                rectangle.translate(-1, 0);
            }
        }
        int[] m = getTouchingLine(firstRow, reverseRow);
        // revert last translation
        moved--;
        for (Rectangle rectangle : reverseRow) {
            Util.animate();
            rectangle.place(true);
            rectangle.translate(1, 0);
        }

        int w_1 = x_0 + w_max + d_1 - moved;   // As in the paper

        // Either no rectangles anymore or reverse row reached far enough
        if (remainingRectangles.isEmpty()) {
            return new Solution(parameters, this);
        }

        // FROM HERE HARD
        int nextLevel = w_1;

        if (m[1] < parameters.height / 2) { // otherwise skip and go to step 5
            // Note that at least two rectangles are placed on the second reverse level
            Rectangle lastOnReverse = reverseRow.get(reverseRow.size() - 1); // r_k in the paper
            Rectangle lastButOneOnReverse = reverseRow.get(reverseRow.size() -2); // r_j in the paper

            reverseRow.remove(lastOnReverse); // Because we want to drop everything except this one
            int H_2 = 0; // as in the paper
            while (getTouchingLine(firstRow, reverseRow).length == 0) { // not touching
                H_2++;
                for (Rectangle rectangle : reverseRow) {
                    rectangle.translate(-1, 0);
                }
            }
            // revert last translation so not touching anymore
            H_2--;
            for (Rectangle rectangle : reverseRow) {
                rectangle.translate(1, 0);
            }

            int x_third_level = lastButOneOnReverse.x + lastButOneOnReverse.width;
            if (H_2 <= lastOnReverse.width) { // what had to be done when H_2 < lastOnReverse.width was not in the paper but Wikipedia said the same as when equal
                // Push up the last rectangle in the reverse row
                while (canPushRectangleUp(firstRow, lastOnReverse)) {
                    lastOnReverse.translate(0, -1);
                }
                // revert last translation
                lastOnReverse.translate(0, 1);
            } else { // (H_2 > lastOnReverse.width) {
                lastOnReverse.setLocation(x_third_level, lastOnReverse.y);
                while (canPushRectangleUp(firstRow, lastOnReverse)) {
                    lastOnReverse.translate(0, -1);
                    lastOnReverse.translate(0, 1);
                }
            }

            nextLevel = x_third_level;
            firstRow.add(lastOnReverse); // To make it easier to check if new rectangles intersect with others
        }

        // STEP 5 #####
        // From here just modified first fit
        firstFit(remainingRectangles, nextLevel, parameters, firstRow);

        assert (remainingRectangles.size() == 0);
        int finalWidth = findNewLevel(firstRow);
        Util.animate();
        return new Solution(parameters, this);
    }

    void firstFit(ArrayList<Rectangle> remainingRectangles, int level, Parameters parameters, ArrayList<Rectangle> firstRow) {
        while (!remainingRectangles.isEmpty()) {
            remainingRectangles.get(0).setLocation(level, parameters.height);
            while (canPushRectangleUp(firstRow, remainingRectangles.get(0))) { // Still sorted by width
                remainingRectangles.get(0).translate(0, -1);
            }
            remainingRectangles.get(0).translate(0, 1);
            if (remainingRectangles.get(0).y + remainingRectangles.get(0).height >= parameters.height) { //   TODO: SEE IF THIS SHOULD BE > OR >=
                // it doesnt fit unfortunately, so we simply make a new level at the right of the fathest block to the right
                int new_level = findNewLevel(firstRow); // Just search for ride side of most right block
                firstFit(remainingRectangles, new_level, parameters, firstRow);
            } else { // rectangle fit in this last level, so we remove it from remaining rectangles but add to firstRow to look for collisions
                firstRow.add(remainingRectangles.get(0));
                remainingRectangles.remove(0);
            }
        }
    }

    int findNewLevel(ArrayList<Rectangle> firstRow) {
        int max_x = 0;
        for (Rectangle rectangle : firstRow) {
            if (rectangle.x + rectangle.width > max_x) {
                max_x = rectangle.x + rectangle.width;
            }
        }
        return max_x;
    }

    boolean canPushRectangleUp(ArrayList<Rectangle> firstRow, Rectangle rectangleToPushUp) {
        rectangleToPushUp.translate(0, -1);
        if (rectangleToPushUp.y <= 0) {
            rectangleToPushUp.translate(0, 1);
            return false;
        }
        for (Rectangle rectangle : firstRow) {
            if (rectangleToPushUp.intersects(rectangle)) {
                rectangleToPushUp.translate(0, 1);
                return false;
            }
        }
        rectangleToPushUp.translate(0, 1);
        return true;
    }

    // CAN BE FASTER FOR SURE
    int[] getTouchingLine(List<Rectangle> leftRow, List<Rectangle> rightRow) {
        for (Rectangle left : leftRow) {
            for (Rectangle right : rightRow) {
                if (left.intersects(right)) {
                    int m_1 = Math.max(left.y, right.y);
                    int m_2 = Math.min(left.y + left.height, right.y + right.height);
                    return new int[]{m_1, m_2};
                }
            }
        }
        // should get here when not touching
        return new int[]{};
    }
}
