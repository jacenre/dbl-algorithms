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
        PositionRectanglePair pair = skylineDataStructure.anyOnlyFit(sequence);
        Assertions.assertTrue(pair != null);
    }

    @Test
    void testOnlyFitFunctionality2() {
        // Geen only fit
        Rectangle notOnlyFitRectangle = new Rectangle(3, 5);
        ArrayList<Rectangle> sequence = new ArrayList<>();
        sequence.add(notOnlyFitRectangle);
        PositionRectanglePair pair2 = skylineDataStructure.anyOnlyFit(sequence);
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

        PositionRectanglePair pair3 = skylineDataStructure.anyOnlyFit(sequence);
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


    @Test
    void firstElaborateExample() {
        ArrayList<Rectangle> sequence = new ArrayList<>();
        sequence.add(new Rectangle(12, 8));
        sequence.add(new Rectangle(10,  9));
        sequence.add(new Rectangle(8,12));
        sequence.add(new Rectangle(16, 3));
        sequence.add(new Rectangle(4, 16));
        sequence.add(new Rectangle(10,6));

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
                toBePlaced.rectangle.x = toBePlaced.position.x;
                toBePlaced.rectangle.y = toBePlaced.position.y;
                skylineDataStructure.adjustSkyline(toBePlaced.rectangle, toBePlaced.position);
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
                skylineDataStructure.adjustSkyline(toBePlaced.rectangle, toBePlaced.position);
            } else {
            }
        }
        System.out.println("nice");
    }

    int globalHeight;


    public boolean hasOverlap(Rectangle rectangle, SegPoint position, ArrayList<Rectangle> sequence) {
        if (position.start) {
            rectangle.x = position.x;
            rectangle.y = position.y;
        } else {
            rectangle.x = position.x;
            rectangle.y = position.y - rectangle.height;
        }

        if (rectangle.y + rectangle.height > globalHeight) {
            //System.out.println("reaches bottom");
            return true;
        } else if (rectangle.y < 0) {
            //System.out.println("reaches top");
            return true;
        }

        rectangle.place(true);
        ArrayList<Rectangle> placedRecs = new ArrayList<>();
        for (Rectangle rec : sequence) {
            if (rec.isPlaced()) {
                placedRecs.add(rec);
            }
        }

        Parameters parameters = new Parameters();
        parameters.rectangles = placedRecs;

        if (Util.sweepline(new Solution(parameters))) {
            //System.out.println("sweepline detected collision");
            rectangle.place(false);
            return true;
        }
        rectangle.place(false);
        return false;
    }
}