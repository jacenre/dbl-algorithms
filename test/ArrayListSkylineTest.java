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

//    @Test
//    void testOnlyFitFunctionality() {
//        // Wel een only fit
//        Rectangle onlyFitRectangle = new Rectangle(3, 10);
//        ArrayList<Rectangle> sequence = new ArrayList<>();
//        sequence.add(onlyFitRectangle);
//        Assertions.assertTrue(skylineDataStructure.anyOnlyFit(sequence));
//
//        // Geen only fit
//        Rectangle notOnlyFitRectangle = new Rectangle(3, 5);
//        sequence.add(notOnlyFitRectangle);
//        Assertions.assertFalse(skylineDataStructure.anyOnlyFit(sequence));
//        skylineDataStructure.addRectangle(notOnlyFitRectangle, skylineDataStructure.getCandidatePoints().get(0));
//
//        // Geen only fit want geen unieke segment-rectangle combi
//        Rectangle unUnique1 = new Rectangle(3, 5);
//        sequence.add(unUnique1);
//        Rectangle unUnique2 = new Rectangle(3, 5);
//        sequence.add(unUnique2);
//
//        Assertions.assertFalse(skylineDataStructure.anyOnlyFit(sequence));
//    }

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

        skylineDataStructure.addRectangle(firstRec, skylineDataStructure.getCandidatePoints().get(0));
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



    @Test
    void firstElaborateExample() {
        ArrayList<Rectangle> sequence = new ArrayList<>();
        sequence.add(new Rectangle(4, 4));
        sequence.add(new Rectangle(3,  4));
        sequence.add(new Rectangle(4 ,3));
        sequence.add(new Rectangle(6, 2));
        sequence.add(new Rectangle(3, 3));
        sequence.add(new Rectangle(5, 1));
        sequence.add(new Rectangle(2, 2));
        sequence.add(new Rectangle(2, 2));
        sequence.add(new Rectangle(2, 2));
        sequence.add(new Rectangle(4, 1));

        int totalArea = 0;
        for (Rectangle rec : sequence) {
            totalArea += rec.height * rec.width;
        }
        System.out.println("Total area: " + totalArea);

        ArrayList<PositionRectanglePair> minimumLocalSpaceWasteRectangles = new ArrayList<>();

        ArrayList<Rectangle> originalSequence = skylineDataStructure.deepCopyRectangles(sequence);
        //while (!sequence.isEmpty()) {
       for (int k = 0; k < 5; k++) {
            int minimumLocalSpaceWaste = Integer.MAX_VALUE;
            minimumLocalSpaceWasteRectangles.clear();
            PositionRectanglePair toBePlaced = null;

//            if (skylineDataStructure.anyOnlyFit(sequence)) {
//                continue;
//            }
            for (SegPoint segPoint : skylineDataStructure.getCandidatePoints()) {
                for (Rectangle rectangle : sequence) {
                    if (skylineDataStructure.testSpreadConstraint(rectangle, segPoint)) { // spread constraint
                        continue;
                    }
                    if (hasOverlap(rectangle, segPoint, sequence)) {
                        continue;
                    }
                    int localSpaceWaste = skylineDataStructure.getLocalWaste(rectangle, segPoint, originalSequence);
                    if (localSpaceWaste < minimumLocalSpaceWaste) {
                        minimumLocalSpaceWasteRectangles.clear();
                        minimumLocalSpaceWaste = localSpaceWaste;
                        minimumLocalSpaceWasteRectangles.add(new PositionRectanglePair(rectangle, segPoint));
                    } else if (localSpaceWaste == minimumLocalSpaceWaste) {
                        minimumLocalSpaceWasteRectangles.add(new PositionRectanglePair(rectangle, segPoint));
                    }
                }
            }

            if (minimumLocalSpaceWasteRectangles.size() == 1) { // minimum local waste
                toBePlaced = minimumLocalSpaceWasteRectangles.get(0);
            } else if (minimumLocalSpaceWasteRectangles.size() >= 2){ // maximum fitness number and earliest in sequence
                int highestFitness = 0;
                toBePlaced = minimumLocalSpaceWasteRectangles.get(0);
                for (PositionRectanglePair pair : minimumLocalSpaceWasteRectangles) {
                    if (skylineDataStructure.getFitnessNumber(pair.rectangle, pair.position) > highestFitness) {
                        toBePlaced = pair;
                    }
                }
            }
            /** debug */
            for (Segment segment : skylineDataStructure.skyline) {
                System.out.println("up" + segment.start);
                System.out.println("down"+ segment.end);
            }
            if (toBePlaced != null) {
                /* Placement of rectangle */
                if (toBePlaced.position.start) {
                    toBePlaced.rectangle.x = toBePlaced.position.x;
                    toBePlaced.rectangle.x = toBePlaced.position.y;
                } else if (!toBePlaced.position.start) {
                    toBePlaced.rectangle.x = toBePlaced.position.x;
                    toBePlaced.rectangle.x = toBePlaced.position.y - toBePlaced.rectangle.height;
                }
                System.out.println("Placed rectangle " + toBePlaced.rectangle + " at location " + toBePlaced.position);
                toBePlaced.rectangle.place(true);
                sequence.remove(toBePlaced.rectangle);
                skylineDataStructure.addRectangle(toBePlaced.rectangle, toBePlaced.position);
            } else {
                //System.out.println("oh shit");
            }
        }
        System.out.println("nice");
    }

    @Test
    void secondElaborateExample() {
        ArrayList<Rectangle> sequence = new ArrayList<>();
        sequence.add(new Rectangle(4, 4));
        sequence.add(new Rectangle(4,  4));
        sequence.add(new Rectangle(4 ,2));

        int totalArea = 0;
        for (Rectangle rec : sequence) {
            totalArea += rec.height * rec.width;
        }
        System.out.println("Total area: " + totalArea);

        ArrayList<PositionRectanglePair> minimumLocalSpaceWasteRectangles = new ArrayList<>();

        ArrayList<Rectangle> originalSequence = skylineDataStructure.deepCopyRectangles(sequence);
        while (!sequence.isEmpty()) {
            int minimumLocalSpaceWaste = Integer.MAX_VALUE;
            minimumLocalSpaceWasteRectangles.clear();
            PositionRectanglePair toBePlaced = skylineDataStructure.anyOnlyFit(sequence);

            if (!(toBePlaced == null)) {
                System.out.println("Placed rectangle " + toBePlaced.rectangle + " at location " + toBePlaced.position);
                skylineDataStructure.addRectangle(toBePlaced.rectangle, toBePlaced.position);
                toBePlaced.rectangle.place(true);
                sequence.remove(toBePlaced.rectangle);
                continue;
            }
            for (SegPoint segPoint : skylineDataStructure.getCandidatePoints()) {
                for (Rectangle rectangle : sequence) {
                    if (skylineDataStructure.testSpreadConstraint(rectangle, segPoint) || hasOverlap(rectangle, segPoint, originalSequence)) { // spread constraint
                        continue;
                    }
                    int localSpaceWaste = skylineDataStructure.getLocalWaste(rectangle, segPoint, sequence);
                    if (localSpaceWaste < minimumLocalSpaceWaste) {
                        minimumLocalSpaceWasteRectangles.clear();
                        minimumLocalSpaceWaste = localSpaceWaste;
                        minimumLocalSpaceWasteRectangles.add(new PositionRectanglePair(rectangle, segPoint));
                    } else if (localSpaceWaste == minimumLocalSpaceWaste) {
                        minimumLocalSpaceWasteRectangles.add(new PositionRectanglePair(rectangle, segPoint));
                    }
                }
            }

            if (minimumLocalSpaceWasteRectangles.size() == 1) { // minimum local waste
                toBePlaced = minimumLocalSpaceWasteRectangles.get(0);
            } else if (minimumLocalSpaceWasteRectangles.size() >= 2){ // maximum fitness number and earliest in sequence
                int highestFitness = 0;
                toBePlaced = minimumLocalSpaceWasteRectangles.get(0);
                for (PositionRectanglePair pair : minimumLocalSpaceWasteRectangles) {
                    if (skylineDataStructure.getFitnessNumber(pair.rectangle, pair.position) > highestFitness) {
                        toBePlaced = pair;
                    }
                }
            }

            if (toBePlaced != null) {
                /* Placement of rectangle */
                if (toBePlaced.position.start) {
                    toBePlaced.rectangle.x = toBePlaced.position.x;
                    toBePlaced.rectangle.y = toBePlaced.position.y;
                } else if (!toBePlaced.position.start) {
                    toBePlaced.rectangle.x = toBePlaced.position.x;
                    toBePlaced.rectangle.y = toBePlaced.position.y - toBePlaced.rectangle.height;
                }
                System.out.println("Placed rectangle " + toBePlaced.rectangle + " at location " + toBePlaced.position);
                toBePlaced.rectangle.place(true);
                sequence.remove(toBePlaced.rectangle);
                skylineDataStructure.addRectangle(toBePlaced.rectangle, toBePlaced.position);
            } else {
                System.out.println("kon niet plaatsen");
            }
        }
        System.out.println("nice");
    }


    public boolean hasOverlap(Rectangle rectangle, SegPoint position, ArrayList<Rectangle> sequence) {
        if (position.start) {
            rectangle.x = position.x;
            rectangle.y = position.y;
        } else {
            rectangle.x = position.x;
            rectangle.y = position.y - rectangle.height;
        }

        rectangle.place(true);
        ArrayList<Rectangle> placedRecs = new ArrayList<>();
        for (Rectangle rec : sequence) {
            if (rec.isPlaced()) {
                placedRecs.add(rec);
            }
        }

        Rectangle extraRec = new Rectangle(0, 10, 10, 1);
        extraRec.place(true);
        placedRecs.add(extraRec);
        Parameters parameters = new Parameters();
        parameters.rectangles = placedRecs;

        if (Util.sweepline(new Solution(parameters))) {
            rectangle.place(false);
            return true;
        }
        rectangle.place(false);
        return false;
    }
}