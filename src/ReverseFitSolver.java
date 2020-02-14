import org.w3c.dom.css.Rect;

import java.util.ArrayList;
import java.util.List;


public class ReverseFitSolver extends AbstractSolver {

    @Override
    Solution optimal(Parameters parameters) {
        ArrayList<Rectangle> remainingRectangles = new ArrayList<>();
        for (Rectangle rectangle : parameters.rectangles) {
            if (rectangle.width > parameters.height / 2) {
                rectangle.rotate();
            }
            remainingRectangles.add(rectangle);
        }

        ArrayList<Rectangle> firstLargeRectangles = new ArrayList<>();   // First rectangles that get taken out

        int x_0 = 0; // Keeps track of where to place the blocks in the while loop
        // Stack all the rectangles that have height > parameters.height/2 next to each other
        for (Rectangle rectangle : remainingRectangles) {
            if (rectangle.height > parameters.height / 2) {
                rectangle.setLocation(x_0, 0);
                firstLargeRectangles.add(rectangle);
                x_0 += rectangle.width;
            }
        }
        remainingRectangles.removeAll(firstLargeRectangles);

        remainingRectangles.sort((o1, o2) -> (o2.width) - (o1.width));
        // The remaining rectangles all have height <= parameters.height/2
        // Sorts the remaining rectangles based on width
        int w_max = remainingRectangles.get(0).width; //Widest of the remaining rectangles
        //Pack the rectangles from up to down along x_0
        int y_0 = 0;
        ArrayList<Rectangle> firstRow = new ArrayList<>();

        // Filling in first row
        for (Rectangle rectangle : remainingRectangles) {
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
            return new Solution(x_0 + w_max, parameters.height, parameters);
        }

        int d_1 = remainingRectangles.get(0).width; // width of the widest remaining rectangles

        ArrayList<Rectangle> reverseRow = new ArrayList<>();
        int y_0_reverse = parameters.height;
        for (Rectangle rectangle : remainingRectangles) {
            if (y_0_reverse < parameters.height / 2) { // If the reverse row is this far up, stop
                break;
            }
            if (rectangle.height + y_0 < parameters.height) {    // Smaller rectangles might still fit in the first row
                rectangle.setLocation(x_0, y_0);
                y_0 += rectangle.height;
                firstRow.add(rectangle);
            } else {                                                  // If it's too big for the first row, then reverse row
                rectangle.setLocation(x_0 + w_max + d_1 - rectangle.width, y_0_reverse - rectangle.height);
                y_0_reverse -= rectangle.height;
                reverseRow.add(rectangle);
            }
        }

        remainingRectangles.removeAll(firstRow);
        remainingRectangles.removeAll(reverseRow);

        // Move all the rectangles from the right row to the left till any of them touch
        int moved = 0;
        while (getTouchingLine(firstRow, reverseRow).length == 0) {
            for (Rectangle rectangle : reverseRow) {
                rectangle.translate(-1, 0);
                moved++;
            }
        }
        int w_1 = x_0 + w_max + d_1 - moved;   // As in the paper

        // Either no rectangles anymore or reverse row reached far enough
        if (remainingRectangles.isEmpty()) {
            return new Solution(Math.max(w_1, x_0 + w_max), parameters.height, parameters);
        }

        // FROM HERE HARD
        if (getTouchingLine(firstRow, reverseRow)[1] >= parameters.height / 2) {
            //go to step 5
        }
        // else if m_2 < parameters.height /2

        Rectangle lastOnReverse = reverseRow.get(reverseRow.size() - 1); // r_k in the paper
        Rectangle lastButOneOnReverse = reverseRow.get(reverseRow.size() -2); // r_j in the paper

        reverseRow.remove(lastOnReverse); // Because we want to drop everything except this one
        int H_2 = 0; // as in the paper
        while (getTouchingLine(firstRow, reverseRow).length == 0) {
            for (Rectangle rectangle : reverseRow) {
                rectangle.translate(-1, 0);
                H_2++;
            }
        }

        int x_third_level = lastButOneOnReverse.x + lastButOneOnReverse.width; // Third level from where we start the regular first fit

        if (H_2 > lastOnReverse.height) {
            lastOnReverse.setLocation(lastButOneOnReverse.x + lastButOneOnReverse.width, lastButOneOnReverse.y);
        }

        while (canPushRectangleUp(firstRow, lastOnReverse)) {   // Push up the last rectangle in the reverse row
            lastOnReverse.translate(0, -1);
        }

        firstRow.add(lastOnReverse); // To make it easier to check if new rectangles intersect with others

        // Step 5
        // From here just modified first fit
        firstFit(remainingRectangles, x_third_level, parameters, firstRow);

        assert (remainingRectangles.size() == 0);
        int finalWidth = findNewLevel(firstRow);

        return new Solution(finalWidth, parameters.height, parameters);
    }

    void firstFit(ArrayList<Rectangle> remainingRectangles, int level, Parameters parameters, ArrayList<Rectangle> firstRow) {
        while (!remainingRectangles.isEmpty()) {
            remainingRectangles.get(0).setLocation(level, parameters.height);
            while (canPushRectangleUp(firstRow, remainingRectangles.get(0))) { // Still sorted by width
                remainingRectangles.get(0).translate(0, -1);
            }
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
        // should never get here
        return new int[]{};
    }
}
