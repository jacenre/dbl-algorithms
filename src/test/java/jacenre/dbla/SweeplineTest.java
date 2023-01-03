package jacenre.dbla;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class SweeplineTest {

    // Trivial overlap
    @Test
    void sweeplineTrue() {
        Parameters parameters = new Parameters();

        ArrayList<Rectangle> rectangles = new ArrayList<>();
        Rectangle rectangle1 = new Rectangle(10, 10);
        Rectangle rectangle2 = new Rectangle(10, 10);

        rectangles.add(rectangle1);
        rectangles.add(rectangle2);

        parameters.rectangles = rectangles;

        Solution solution = new Solution(parameters);
        assertTrue(Util.sweepline(solution));
    }

    // Trivial overlap
    @Test
    void sweeplineTrue2() {
        Parameters parameters = new Parameters();

        ArrayList<Rectangle> rectangles = new ArrayList<>();
        Rectangle rectangle1 = new Rectangle(10, 10);
        Rectangle rectangle2 = new Rectangle(10, 10);

        rectangle2.x = 5;
        rectangle2.y = 5;

        rectangles.add(rectangle1);
        rectangles.add(rectangle2);

        parameters.rectangles = rectangles;

        Solution solution = new Solution(parameters);
        assertTrue(Util.sweepline(solution));
    }

    // Trivial overlap
    @Test
    void sweeplineFalse() {
        Parameters parameters = new Parameters();

        ArrayList<Rectangle> rectangles = new ArrayList<>();
        Rectangle rectangle1 = new Rectangle(10, 10);
        Rectangle rectangle2 = new Rectangle(10, 10);

        rectangle2.x = 10;
        rectangle2.y = 10;

        rectangles.add(rectangle1);
        rectangles.add(rectangle2);

        parameters.rectangles = rectangles;

        Solution solution = new Solution(parameters);
        assertFalse(Util.sweepline(solution));
    }

    // Trivial overlap
    @Test
    void sweeplineFalse2() {
        Parameters parameters = new Parameters();

        ArrayList<Rectangle> rectangles = new ArrayList<>();
        Rectangle rectangle1 = new Rectangle(10, 10);
        Rectangle rectangle2 = new Rectangle(10, 10);

        rectangle2.x = 15;
        rectangle2.y = 15;

        rectangles.add(rectangle1);
        rectangles.add(rectangle2);

        parameters.rectangles = rectangles;

        Solution solution = new Solution(parameters);
        assertFalse(Util.sweepline(solution));
    }
}