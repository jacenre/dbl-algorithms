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
    Parameters parameters;

    // Algorithm 2 in the paper
    @Override
    Solution pack(Parameters parameters) {
        this.parameters = parameters;
        int lowerBound = getLowerBound(parameters);
        int upperBound = Math.max(lowerBound + 1, (int) (lowerBound * 1.1));
        int iter = 1;
        boolean upperBoundFound = false;

        while (/*time limit not reached and */ lowerBound != upperBound) {
            int tempLowerBound = lowerBound;
            while (tempLowerBound < upperBound) {
                // Binary search
                int width = ((tempLowerBound + upperBound) / 2);
                if (solve(parameters, width, iter)) {
                    /* record this solution */
                    upperBound = width;
                    upperBoundFound = true;
                    return globalSolution;
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
            }
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
     * @param W          The width that has been supplied by the pack method
     * @param iter       The iterations variant that has also been supplied by the pack method
     * @returns a boolean signalling if a solution could be found with the given attributes
     */
    boolean solve(Parameters parameters, int W, int iter) {
        // DEBUG

//        if (heuristicSolve(parameters.rectangles, W, W)) {
//            return true;
//        }

        // DEBUG
        for (ArrayList<Rectangle> seq : new RectangleSorter(parameters.rectangles)) {
            for (float ms : new SpreadValues(seq, parameters)) {
                if (heuristicSolve(seq, W, (int) ms)) {
                    return true;
                }
                // Map the remaining tabu iterations against the set of tabus.
                HashMap<Integer, Integer> tabu = new HashMap<>();
                for (int i = 0; i < iter; i++) {
                    tabu.remove(i);
                    // Seqx is the sequence with highest area utilization.
                    ArrayList<Rectangle> seqx = null;
                    int highestAreaUtil = 0;

                    HashMap<Integer, Boolean> solved = new HashMap<>();
                    for (ArrayList<Rectangle> rectangles : new TabuSearchGenerator(tabu.values(), 10, seq)) {
                        if (heuristicSolve(rectangles, W, (int) ms)) {
                            solved.put(rectangles.hashCode(), true);
                        }

                        int areaUtil = parameters.height / Util.maxHeight(rectangles);

                        if (seqx == null || areaUtil > highestAreaUtil) {
                            seqx = rectangles;
                            highestAreaUtil = areaUtil;
                        }
                    }
                    if (solved.get(seqx.hashCode())) {
                        return true;
                    }
                    tabu.put(i + 3 * parameters.rectangles.size(), seqx.hashCode());
                    seq = seqx;
                }
            }
        }
        return false;
    }


    /**
     * Returns an iterator over an amount of sequences using Tabu.
     */
    static class TabuSearchGenerator implements Iterable<ArrayList<Rectangle>> {

        ArrayList<ArrayList<Rectangle>> sequences;

        int MAX_ATTEMPTS = 100;
        Random random = new Random(100L);

        /**
         * Constructor
         *
         * @param tabu       set containing all tabu sequences
         * @param n          amount of sequences to generate
         * @param rectangles the Rectangles list for which to generate
         */
        TabuSearchGenerator(Collection<Integer> tabu, int n, ArrayList<Rectangle> rectangles) {
            sequences = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < MAX_ATTEMPTS; j++) {
                    ArrayList<Rectangle> copy = Util.cloneRectangleState(rectangles);
                    int a = random.nextInt(copy.size() - 1);
                    int b = random.nextInt(copy.size() - 1);

                    if (a == b) b++;

                    Rectangle temp = copy.get(a).copy();
                    copy.set(a, copy.get(b).copy());
                    copy.set(b, temp);

                    if (!tabu.contains(copy.hashCode())) {
                        sequences.add(copy);
                        break;
                    }
                }
            }
        }

        /**
         * Returns an iterator over elements of type {@code sequences}.
         *
         * @return an Iterator.
         */
        @Override
        public Iterator<ArrayList<Rectangle>> iterator() {
            return sequences.iterator();
        }
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
         * Returns an iterator over elements of type {@code ArrayList<Rectangle>}.
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
    static class SpreadValues implements Iterable<Float> {

        ArrayList<Float> spreadValues = new ArrayList<>();

        /**
         * Initialize and calculate the spread values.
         *
         * @param rectangles the Rectangles for which to calculate
         */
        SpreadValues(ArrayList<Rectangle> rectangles, Parameters parameters) {
            // Maximum height
            float mh = 0;
            for (Rectangle rectangle : rectangles) {
                mh = (rectangle.height > mh) ? rectangle.height : mh;
            }

            // {mh, mh + (H - mh) * 1/3, mh + (H - mh) * 2/3, H}
            spreadValues.add(mh);
            spreadValues.add(mh + (parameters.height - mh) * (1f / 3f));
            spreadValues.add(mh + (parameters.height - mh) * (2f / 3f));
            spreadValues.add((float) parameters.height);
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

    public void resetRecs(ArrayList<Rectangle> sequence) {
        for (Rectangle rec : sequence) {
            rec.place(false);
        }
    }

    /**
     * Goes through the heuristics and places the sequence of rectangles in the box while maintaining a skyline view of
     * the whole ordeal. Returns if a solution was able to be found with the given maximumSpread and width.
     *
     * @param originalSequence The sequence of rectangles, which can be very different according to different sorting and
     *                         the random permutations made by the tabu search algorithm
     * @param width            The given width to which to adhere
     * @param maximumSpread    The given maximumSpread to which to adhere
     * @return true or false whether the heuristic was able to pack all the rectangles given the restrictions
     */
    boolean heuristicSolve(ArrayList<Rectangle> originalSequence, int width, int maximumSpread) {
        resetRecs(originalSequence);
        // Test all the candidate positions - rectangle combos
        ArrayListSkyline skylineDataStructure = new ArrayListSkyline(parameters.height, width, maximumSpread);
        ArrayList<PositionRectangleRotationPair> minimumLocalSpaceWasteRectangles = new ArrayList<>();
        ArrayList<Rectangle> sequence = skylineDataStructure.deepCopyRectangles(originalSequence);

        // Place a rectangle every time till every rectangle is placed
        while (!sequence.isEmpty()) {
            int minimumLocalSpaceWaste = Integer.MAX_VALUE;
            minimumLocalSpaceWasteRectangles.clear();
            // Test if there is any perfect place to place the rectangle.
            PositionRectangleRotationPair toBePlaced = skylineDataStructure.anyOnlyFit(sequence);

            // If there is then place it
            if (!(toBePlaced == null)) {
                placeRectangle(toBePlaced, sequence, skylineDataStructure);
                continue;
            }

            // Check for every rectangle-position pair the local waste
            for (SegPoint segPoint : skylineDataStructure.getCandidatePoints()) {
                for (Rectangle rectangle : sequence) {
                    for (int i = 0; i < 2; i++) {
                        if (i == 1) {
                            rectangle.rotate();
                        }
                        // Checks if the rectangle can be placed
                        if (skylineDataStructure.testSpreadConstraint(rectangle, segPoint) || hasOverlap(rectangle, segPoint, width, originalSequence)) { // spread constraint
                            continue;
                        }
                        int localSpaceWaste = skylineDataStructure.getLocalWaste(rectangle, segPoint, sequence);
                        if (localSpaceWaste < minimumLocalSpaceWaste) {
                            minimumLocalSpaceWasteRectangles.clear();
                            minimumLocalSpaceWaste = localSpaceWaste;
                            minimumLocalSpaceWasteRectangles.add(new PositionRectangleRotationPair(rectangle, segPoint, (i == 1) ? true : false));
                        } else if (localSpaceWaste == minimumLocalSpaceWaste) {
                            minimumLocalSpaceWasteRectangles.add(new PositionRectangleRotationPair(rectangle, segPoint, (i == 1) ? true : false));
                        }
                    }
                    rectangle.rotate();
                }
            }


            if (minimumLocalSpaceWasteRectangles.size() == 1) { // minimum local waste
                toBePlaced = minimumLocalSpaceWasteRectangles.get(0);
            } else if (minimumLocalSpaceWasteRectangles.size() >= 2) { // maximum fitness number and earliest in sequence
                int highestFitness = 0;
                toBePlaced = minimumLocalSpaceWasteRectangles.get(0);
                for (PositionRectangleRotationPair pair : minimumLocalSpaceWasteRectangles) {
                    if (skylineDataStructure.getFitnessNumber(pair) > highestFitness) {
                        toBePlaced = pair;
                    }
                }
            }

            if (toBePlaced != null) {
                /* Placement of rectangle */
                placeRectangle(toBePlaced, sequence, skylineDataStructure);
            } else {
                return false;
            }
        }
        parameters.rectangles = originalSequence;
        Solution currentSolution = new Solution(parameters, this);
        System.out.println("solution with width " + width + " maximum spread " + maximumSpread);
        if (globalSolution == null || currentSolution.getArea() < globalSolution.getArea()) {
            globalSolution = currentSolution;
        }
        return true;
    }

    public void placeRectangle(PositionRectangleRotationPair toBePlaced, ArrayList<Rectangle> sequence, ArrayListSkyline skyline) {
        if (toBePlaced.rotated) {
            toBePlaced.rectangle.rotate();
        }
        if (toBePlaced.position.start) {
            toBePlaced.rectangle.x = toBePlaced.position.x;
            toBePlaced.rectangle.y = toBePlaced.position.y;
        } else if (!toBePlaced.position.start) {
            toBePlaced.rectangle.x = toBePlaced.position.x;
            toBePlaced.rectangle.y = toBePlaced.position.y - toBePlaced.rectangle.height;
        }

        skyline.adjustSkyline(toBePlaced.rectangle, toBePlaced.position);
        skyline.mergeSegmentsNextToEachOther();
//        for (Segment seg : skyline.skyline) {
//            for (int j = 0; j < seg.end.y - seg.start.y; j++) {
//                for (int i = 0; i < seg.start.x ; i++) {
//                    System.out.print(" ");
//                }
//                System.out.println(seg.start.x);
//            }
//        }
//        System.out.println("REEEEEEEEWIND");
        toBePlaced.rectangle.place(true);
        sequence.remove(toBePlaced.rectangle);
    }

    public boolean hasOverlap(Rectangle rectangle, SegPoint position, int width, ArrayList<Rectangle> originalSequence) {
        if (position.start) {
            rectangle.x = position.x;
            rectangle.y = position.y;
        } else {
            rectangle.x = position.x;
            rectangle.y = position.y - rectangle.height;
        }

        if (rectangle.y + rectangle.height > parameters.height) {
            //System.out.println("reaches bottom");
            return true;
        } else if (rectangle.y < 0) {
            //System.out.println("reaches top");
            return true;
        } else if (rectangle.x + rectangle.width > width) {
            return true;
        }

        rectangle.place(true);
        for (Rectangle rectangle1 : originalSequence) {
            for (Rectangle rectangle2 : originalSequence) {
                if (rectangle1.isPlaced() && rectangle2.isPlaced() && rectangle1 != rectangle2) {
                    if (rectangle1.intersects(rectangle2)) {
                        rectangle.place(false);
                        return true;
                    }
                }
            }
        }

        rectangle.place(true);
        ArrayList<Rectangle> placedRecs = new ArrayList<>();
        for (Rectangle rec : originalSequence) {
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
