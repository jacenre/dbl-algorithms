import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;

class ArrayListSkylineTest {
    ArrayListSkyline skylineDataStructure;
    @BeforeEach
    void setUp() {skylineDataStructure = new ArrayListSkyline(10, 10, 5);
    }

    @Test
    void testConstruction() {
        Assertions.assertEquals(skylineDataStructure.skyline.size(), 1);
        ArrayList<SegPoint> points = skylineDataStructure.getCandidatePoints();
        Assertions.assertEquals(points.get(0).x, 0);
        Assertions.assertEquals(points.get(0).y, 0);
        Assertions.assertTrue(points.get(0).start);
        Assertions.assertEquals(points.get(1).x, 0);
        Assertions.assertEquals(points.get(1).y, 10);
        Assertions.assertFalse(points.get(1).start);
    }

    @Test
    void testOnlyFitFunctionality() {
        // Wel een only fit
        Rectangle onlyFitRectangle = new Rectangle(3, 10);
        ArrayList<Rectangle> sequence = new ArrayList<>();
        sequence.add(onlyFitRectangle);
        Assertions.assertTrue(skylineDataStructure.anyOnlyFit(sequence));

        // Geen only fit
        Rectangle notOnlyFitRectangle = new Rectangle(3, 5);
        sequence.add(notOnlyFitRectangle);
        Assertions.assertFalse(skylineDataStructure.anyOnlyFit(sequence));
        skylineDataStructure.addRectangle(notOnlyFitRectangle, skylineDataStructure.getCandidatePoints().get(0));

        // Geen only fit want geen unieke segment-rectangle combi
        Rectangle unUnique1 = new Rectangle(3, 5);
        sequence.add(unUnique1);
        Rectangle unUnique2 = new Rectangle(3, 5);
        sequence.add(unUnique2);

        Assertions.assertFalse(skylineDataStructure.anyOnlyFit(sequence));
    }

    @Test
    void testGetAreaOfSkyline1() {
        Rectangle notOnlyFitRectangle = new Rectangle(3, 5);
        skylineDataStructure.addRectangle(notOnlyFitRectangle, skylineDataStructure.getCandidatePoints().get(0));

        Assertions.assertEquals(skylineDataStructure.getAreaOfSkyline(skylineDataStructure.skyline), 15);
        Assertions.assertEquals(skylineDataStructure.getMostLeftPoint(), 0);

        Rectangle onlyFitRectangle = new Rectangle(3, 10);
        skylineDataStructure.addRectangle(onlyFitRectangle, skylineDataStructure.getCandidatePoints().get(0));

        Assertions.assertEquals(skylineDataStructure.getAreaOfSkyline(skylineDataStructure.skyline), 6 * 10);
        Assertions.assertEquals(skylineDataStructure.getMostLeftPoint(), 6);
    }

    @Test
    void testGetAreaOfSkyline2() {
        Rectangle onlyFitRectangle = new Rectangle(3, 10);
        skylineDataStructure.addRectangle(onlyFitRectangle, skylineDataStructure.getCandidatePoints().get(0));

        Assertions.assertEquals(skylineDataStructure.getAreaOfSkyline(skylineDataStructure.skyline), 30);
        Assertions.assertEquals(skylineDataStructure.getMostLeftPoint(), 3);

        Rectangle notOnlyFitRectangle = new Rectangle(3, 5);
        skylineDataStructure.addRectangle(notOnlyFitRectangle, skylineDataStructure.getCandidatePoints().get(0));

        Assertions.assertEquals(skylineDataStructure.getAreaOfSkyline(skylineDataStructure.skyline), 45);
        Assertions.assertEquals(skylineDataStructure.getMostLeftPoint(), 3);
    }

}