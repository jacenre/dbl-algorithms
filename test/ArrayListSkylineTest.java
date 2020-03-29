import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class ArrayListSkylineTest {
    ArrayListSkyline skylineDataStructure;
    @BeforeEach
    void setUp() {skylineDataStructure = new ArrayListSkyline(22, 22, 16);
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
        PositionRectangleRotationPair pair = skylineDataStructure.anyOnlyFit(sequence);
        Assertions.assertTrue(pair != null);
    }

    @Test
    void testOnlyFitFunctionality2() {
        // Geen only fit
        Rectangle notOnlyFitRectangle = new Rectangle(3, 5);
        ArrayList<Rectangle> sequence = new ArrayList<>();
        sequence.add(notOnlyFitRectangle);
        PositionRectangleRotationPair pair2 = skylineDataStructure.anyOnlyFit(sequence);
        Assertions.assertTrue(pair2 == null);
    }

    @Test
    void testOnlyFitFunctionality3() {
        ArrayList<Rectangle> sequence = new ArrayList<>();
        // Geen only fit want geen unieke segment-rectangle combi
        Rectangle unUnique1 = new Rectangle(3, 5);
        sequence.add(unUnique1);
        Rectangle unUnique2 = new Rectangle(3, 5);
        sequence.add(unUnique2);

        PositionRectangleRotationPair pair3 = skylineDataStructure.anyOnlyFit(sequence);
        Assertions.assertTrue(pair3 == null);
    }

    @Test
    void testGetAreaOfSkyline1() {
        Rectangle notOnlyFitRectangle = new Rectangle(3, 5);
        skylineDataStructure.adjustSkyline(notOnlyFitRectangle, skylineDataStructure.getCandidatePoints().get(0));

        Assertions.assertEquals(skylineDataStructure.getAreaOfSkyline(skylineDataStructure.skyline), 15);
        Assertions.assertEquals(skylineDataStructure.getMostLeftPoint(), 0);

        Rectangle onlyFitRectangle = new Rectangle(3, 10);
        skylineDataStructure.adjustSkyline(onlyFitRectangle, skylineDataStructure.getCandidatePoints().get(0));

        Assertions.assertEquals(skylineDataStructure.getAreaOfSkyline(skylineDataStructure.skyline), 6 * 10);
        Assertions.assertEquals(skylineDataStructure.getMostLeftPoint(), 6);
    }

    @Test
    void testGetAreaOfSkyline2() {
        Rectangle onlyFitRectangle = new Rectangle(3, 10);
        skylineDataStructure.adjustSkyline(onlyFitRectangle, skylineDataStructure.getCandidatePoints().get(0));

        Assertions.assertEquals(skylineDataStructure.getAreaOfSkyline(skylineDataStructure.skyline), 30);
        Assertions.assertEquals(skylineDataStructure.getMostLeftPoint(), 3);

        Rectangle notOnlyFitRectangle = new Rectangle(3, 5);
        skylineDataStructure.adjustSkyline(notOnlyFitRectangle, skylineDataStructure.getCandidatePoints().get(0));

        Assertions.assertEquals(skylineDataStructure.getAreaOfSkyline(skylineDataStructure.skyline), 45);
        Assertions.assertEquals(skylineDataStructure.getMostLeftPoint(), 3);
    }

    @Test
    void testLocalWasteFunctionality() {
        Rectangle firstRec = new Rectangle(3, 8);
        Rectangle secondRec = new Rectangle(3, 3);
        ArrayList<Rectangle> sequence = new ArrayList<>();
        sequence.add(firstRec);
        sequence.add(secondRec);

        Assertions.assertEquals(skylineDataStructure.getLocalWaste(firstRec, skylineDataStructure.getCandidatePoints().get(0), sequence), 3*2);
    }

    @Test
    void testLocalWasteFunctionality2() {
        Rectangle firstRec = new Rectangle(3, 8);
        Rectangle secondRec = new Rectangle(3, 3);
        ArrayList<Rectangle> sequence = new ArrayList<>();
        sequence.add(firstRec);
        sequence.add(secondRec);

        Assertions.assertEquals(skylineDataStructure.getLocalWaste(firstRec, skylineDataStructure.getCandidatePoints().get(1), sequence), 3*2);
    }

    @Test
    void testLocalWasteFunctionality3() {
        Rectangle firstRec = new Rectangle(8, 8);
        Rectangle secondRec = new Rectangle(3, 3);
        ArrayList<Rectangle> sequence = new ArrayList<>();
        sequence.add(firstRec);
        sequence.add(secondRec);

        Assertions.assertEquals(skylineDataStructure.getLocalWaste(firstRec, skylineDataStructure.getCandidatePoints().get(0), sequence), (8*2 + 8*2));
    }

    @Test
    void testLocalWasteFunctionality4() {
        Rectangle firstRec = new Rectangle(8, 4);
        Rectangle secondRec = new Rectangle(3, 3);
        ArrayList<Rectangle> sequence = new ArrayList<>();
        sequence.add(firstRec);
        sequence.add(secondRec);

        Assertions.assertEquals(skylineDataStructure.getLocalWaste(secondRec, skylineDataStructure.getCandidatePoints().get(0), sequence), 7*3);
    }

    @Test
    void testLocalWasteFunctionality5() {
        Rectangle firstRec = new Rectangle(2, 8);
        Rectangle secondRec = new Rectangle(2, 10);
        ArrayList<Rectangle> sequence = new ArrayList<>();
        sequence.add(firstRec);
        sequence.add(secondRec);

        skylineDataStructure.adjustSkyline(firstRec, skylineDataStructure.getCandidatePoints().get(0));
        firstRec.place(true);
        sequence.remove(firstRec);
        Assertions.assertTrue(firstRec.isPlaced());

        Assertions.assertEquals(skylineDataStructure.getLocalWaste(secondRec, skylineDataStructure.getCandidatePoints().get(0), sequence), 64);
    }

    @Test
    void testSpreadConstraint() {
        Rectangle firstRec = new Rectangle(6, 8);

        Assertions.assertTrue(skylineDataStructure.testSpreadConstraint(firstRec, skylineDataStructure.getCandidatePoints().get(0)));

        Rectangle secondRec = new Rectangle(5, 7);

        Assertions.assertFalse(skylineDataStructure.testSpreadConstraint(secondRec, skylineDataStructure.getCandidatePoints().get(0)));
    }




}