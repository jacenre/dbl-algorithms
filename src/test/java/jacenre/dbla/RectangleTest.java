package jacenre.dbla;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jacenre.dbla.Rectangle;

class RectangleTest {
    Rectangle rect;
    @BeforeEach
    void setUp() {
        rect = new Rectangle(2, 5);
    }

    @Test
    void testConstruction() {
        Assertions.assertFalse(rect.isRotated());
        Assertions.assertEquals(rect.getWidth(), 2);
        Assertions.assertEquals(rect.getHeight(), 5);
    }

    @Test
    void testRotate() {
        Assertions.assertFalse(rect.isRotated());
        rect.rotate();
        Assertions.assertTrue(rect.isRotated());
        Assertions.assertEquals(rect.getWidth(), 5);
        Assertions.assertEquals(rect.getHeight(), 2);
    }
}