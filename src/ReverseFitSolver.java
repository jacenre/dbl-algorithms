import java.util.ArrayList;
import java.util.List;


public class ReverseFitSolver extends AbstractSolver {

    @Override
    Solution optimal(Parameters parameters) {
        ArrayList<Rectangle> remainingRectangles = new ArrayList<>();
        for (Rectangle rectangle : parameters.rectangles) {
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
        for (Rectangle rectangle: remainingRectangles) {
            if (y_0_reverse < parameters.height / 2) { // If the reverse row is this far up, stop
                break;
            }
            if (rectangle.height + y_0 < parameters.height){    // Smaller rectangles might still fit in the first row
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
        while (!touchingRows(firstRow, reverseRow)) {
            for (Rectangle rectangle : reverseRow) {
                rectangle.x -= 1;
                moved++;
            }
        }
        int w_1 = x_0 + w_max + d_1 - moved;

        // Either no rectangles anymore or reverse row reached far enough
        if (remainingRectangles.isEmpty()) {
            return new Solution(w_1, parameters.height, parameters);
        }

        // FROM HERE HARD
        
        int new_x = Math.max(w_1, x_0 + w_max);
        // Use code of FirstFitSolver to solve the rest with first fit

        return new Solution(new_x , parameters.height, parameters);
    }

    // CAN BE FASTER FOR SURE
    boolean touchingRows(List<Rectangle> leftRow, List<Rectangle> rightRow) {
        for (Rectangle left : leftRow) {
            for (Rectangle right : rightRow) {
                if (touchingRectangles(left, right)){
                    return true;
                }
            }
        }
        return false;
    }

    boolean touchingRectangles(Rectangle left, Rectangle right) {
        boolean leftTopInRight = left.y <= right.y + right.height && left.y >= right.y;
        boolean rightTopInLeft = right.y <= left.y + left.height && right.y >= left.y;
        return  (leftTopInRight || rightTopInLeft) && left.x + left.width == right.x - 1;
    }
}
