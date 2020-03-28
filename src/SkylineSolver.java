import java.util.ArrayList;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Solver algorithm using the Skyline heuristic
 *
 * @see <a href="https://www-sciencedirect-com.dianus.libr.tue.nl/science/article/pii/S0377221711005510">source</a>
 */
public class SkylineSolver extends AbstractSolver {

    Solution globalSolution;
    int globalHeight;
    int globalWidth;

    // Algorithm 2 in the paper
    @Override
    Solution pack(Parameters parameters) {
        globalWidth = 100;
        if (heuristicSolve(parameters.rectangles, globalWidth, 100)) {
            return new Solution(parameters);
        }
        globalHeight = parameters.height;
        int lowerBound = getLowerBound(parameters);
        int upperBound = (int) (lowerBound * 1.1);
        int iter = 1;
        boolean upperBoundFound = false;

        while (/*time limit not reached and */ lowerBound != upperBound) {
            int tempLowerBound = lowerBound;
            while (tempLowerBound < upperBound) {
                int width = ((tempLowerBound + upperBound) / 2);
                if (solve(parameters, width, iter)) {
                    /* record this solution */
                    upperBound = width;
                    upperBoundFound = true;
                } else {
                    tempLowerBound = width + 1;
                }
            }
            if (upperBoundFound == false)
                upperBound = (int) (upperBound * 1.1);
            iter *= 2;
        }
        return globalSolution;
    }

    /**
     * Gives the lowerbound for the binary search for a suitable width of the square area that is used in the second
     * algorithm.
     *
     * @param parameters
     * @return The lowerbound, which is dependent on rotations variant and the given rectangles
     */
    int getLowerBound(Parameters parameters) {
        int totalArea = 0;
        int LB2 = 0;
        double LB3 = 0;
        for (Rectangle rec : parameters.rectangles) {
            totalArea += rec.getHeight() * rec.getWidth();
            LB2 += rec.getWidth();
            if (rec.height == parameters.height / 2) {
                LB3 += rec.width;
            };
        }
        int LB1 = (int) Math.ceil(totalArea / (double) parameters.height);

        if (parameters.rotationVariant) {
            return LB1;
        }

        return Math.max(LB1, LB2 + (int) Math.ceil(LB3 / 2));
    }

    /**
     * Handles a Tabu search for a good sequence of rectangles and good maximum spread.
     *
     * @param parameters the Parameters for which to solve
     * @param W    The width that has been supplied by the pack method
     * @param iter The iterations variant that has also been supplied by the pack method
     * @returns a boolean signalling if a solution could be found with the given attributes
     */
    boolean solve(Parameters parameters, int W, int iter) {
        for (ArrayList<Rectangle> seq : new RectangleSorter(parameters.rectangles)) {
            for (float ms : new SpreadValues(seq)) {
                if (heuristicPacking(seq, W, (int) ms)) {
                    return true;
                }
                for (int i = 0; i < iter; i++) {

                }
            }
        }
        return false;
    }

    /**
     * Iterable that returns deep copies of the given rectangle ArrayList following the sorting rules.
     */
    static class RectangleSorter implements Iterable<ArrayList<Rectangle>> {

        /**
         * ArrayList containing all the different sorted ArrayList.
         */
        ArrayList<ArrayList<Rectangle>> sortedRectangles;

        /**
         * Initialize and sort the Rectangle ArrayLists.
         *
         * @param rectangles the Rectangles to sort
         */
        RectangleSorter(ArrayList<Rectangle> rectangles) {
            sortedRectangles = new ArrayList<>();

            // Area in decreasing order.
            ArrayList<Rectangle> decreasingArea = Util.cloneRectangleState(rectangles);
            decreasingArea.sort(Comparator.comparingInt((Rectangle rect) -> rect.width * rect.height).reversed());
            sortedRectangles.add(decreasingArea);

            // Width in decreasing order.
            ArrayList<Rectangle> decreasingWidth = Util.cloneRectangleState(rectangles);
            decreasingWidth.sort(Comparator.comparingInt((Rectangle rect) -> rect.width).reversed());
            sortedRectangles.add(decreasingWidth);

            // Height in decreasing order.
            ArrayList<Rectangle> decreasingHeight = Util.cloneRectangleState(rectangles);
            decreasingHeight.sort(Comparator.comparingInt((Rectangle rect) -> rect.height).reversed());
            sortedRectangles.add(decreasingHeight);

            // Perimeter in decreasing order.
            ArrayList<Rectangle> decreasingPerimeter = Util.cloneRectangleState(rectangles);
            decreasingPerimeter.sort(Comparator.comparingInt((Rectangle rect) -> 2 * rect.height + 2 * rect.width).reversed());
            sortedRectangles.add(decreasingPerimeter);

            // Max of width en height in decreasing order.
            ArrayList<Rectangle> decreasingMaxSizes = Util.cloneRectangleState(rectangles);
            decreasingMaxSizes.sort(Comparator.comparingInt((Rectangle rect) -> Math.max(rect.width, rect.height)).reversed());
            sortedRectangles.add(decreasingMaxSizes);

            // Diagonal in decreasing order.
            ArrayList<Rectangle> decreasingDiagonal = Util.cloneRectangleState(rectangles);
            decreasingDiagonal.sort(Comparator.comparingInt((Rectangle rect) -> {
                return (int) Math.sqrt(rect.width ^ 2 + rect.height ^ 2) + rect.width + rect.height;
            }).reversed());
            sortedRectangles.add(decreasingDiagonal);
        }

        /**
         * Returns an iterator over elements of type {@code Rectangle}.
         *
         * @return an Iterator.
         */
        @Override
        public Iterator<ArrayList<Rectangle>> iterator() {
            return sortedRectangles.iterator();
        }
    }

    /**
     * Iterable that returns the maximum spread values for an ArrayList of rectangles.
     */
    class SpreadValues implements Iterable<Float> {

        ArrayList<Float> spreadValues = new ArrayList<>();

        /**
         * Initialize and calculate the spread values.
         *
         * @param rectangles the Rectangles for which to calculate
         */
        SpreadValues(ArrayList<Rectangle> rectangles) {
            // Maximum height
            float mh = 0;
            for (Rectangle rectangle : rectangles) {
                mh = (rectangle.height > mh) ? rectangle.height : mh;
            }

            // {mh, mh + (H - mh) * 1/3, mh + (H - mh) * 2/3, H}
            spreadValues.add(mh);
            spreadValues.add(mh + (globalHeight - mh) * (1f / 3f));
            spreadValues.add(mh + (globalHeight - mh) * (2f / 3f));
            spreadValues.add((float) globalHeight);
        }

        /**
         * Returns an iterator over elements of type {@code Float}.
         *
         * @return an Iterator.
         */
        @Override
        public Iterator<Float> iterator() {
            return spreadValues.iterator();
        }
    }


    class PositionRectanglePair {
        Rectangle rectangle;
        SegPoint position;

        PositionRectanglePair(Rectangle rec, SegPoint pnt) {
            this.rectangle = rec;
            this.position = pnt;
        }
    }

    /**
     * Goes through the heuristics and places the sequence of rectangles in the box while maintaining a skyline view of
     * the whole ordeal. Returns if a solution was able to be found with the given maximumSpread and width.
     *
     * @param sequence      The sequence of rectangles, which can be very different according to different sorting and
     *                      the random permutations made by the tabu search algorithm
     * @param width         The given width to which to adhere
     * @param maximumSpread The given maximumSpread to which to adhere
     * @return true or false whether the heuristic was able to pack all the rectangles given the restrictions
     */
    boolean heuristicSolve(ArrayList<Rectangle> sequence, int width, int maximumSpread) {
        // Test all the candidate positions - rectangle combos
        ArrayListSkyline skylineDataStructure = new ArrayListSkyline(globalHeight, width, maximumSpread);
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
                System.out.println("Placed special rectangle " + toBePlaced.rectangle + " at location " + toBePlaced.position);
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
                return false;
            }
        }
        System.out.println("nice");
        return true;
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

        Rectangle extraRec = new Rectangle(0, globalHeight, globalWidth, 1);
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
