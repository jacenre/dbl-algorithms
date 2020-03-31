import java.util.ArrayList;

import java.util.*;

/**
 * Solver algorithm using the Skyline heuristic
 *
 * @see <a href="https://www-sciencedirect-com.dianus.libr.tue.nl/science/article/pii/S0377221711005510">source</a>
 */
public class SkylineSolver extends AbstractSolver {

    Solution globalSolution;
    Parameters parameters;

    @Override
    Set<Util.HeightSupport> getHeightSupport() {
        return new HashSet<>(Arrays.asList(Util.HeightSupport.FIXED, Util.HeightSupport.FREE));
    }

    @Override
    public boolean canSolveParameters(Parameters parameters) {
        if (parameters.rectangles.size() > 100) return false;
        if ((parameters.heightVariant == Util.HeightSupport.FREE || parameters.freeHeightUtil)  && parameters.rectangles.size() > 50) return false;
        return super.canSolveParameters(parameters);
    }

    private int debug = 0;
    private int numChecks;

    int getNumChecks(Parameters parameters) {
        if (parameters.freeHeightUtil || parameters.heightVariant == Util.HeightSupport.FREE) return 3000;
        return 1500;
    }

    // Algorithm 2 in the paper
    @Override
    Solution pack(Parameters parameters) {
        this.parameters = parameters;
        int lowerBound = getLowerBound(parameters);

        globalSolution = new FirstFitSolver().getSolution(parameters);
        if (globalSolution.getRate() == 1.0d) return globalSolution;

        int upperBound = (int) globalSolution.getWidth();

        int iter = 1;
        debug = 0;

        numChecks = getNumChecks(parameters); // amount of checks that can be done

        terminate:
        while (numChecks > 0 && lowerBound != upperBound) {
//            System.out.println(lowerBound + " - " + upperBound + ", " + debug++);
            int tempLowerBound = lowerBound;
            while (tempLowerBound < upperBound) {
                // Binary search
                int width = ((tempLowerBound + upperBound) / 2);
//                System.out.println("Solving for width=" + width + ", and " + iter + " iterations");
                if (solve(parameters, width, iter)) {
                    if (!(numChecks > 0)) break terminate;
//                    System.out.println("solution found with width " + width);
                    /* record this solution */
                    if (globalSolution.getRate() == 1.0d) {
                        return globalSolution;
                    }
                    upperBound = width;
                } else {
                    tempLowerBound = width + 1;
                }
            }
            iter *= 2;
        }
        globalSolution.solvedBy = this;
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
        int LB4 = 0;
        for (Rectangle rec : parameters.rectangles) {
            totalArea += rec.getHeight() * rec.getWidth();
            if (rec.getHeight() > parameters.height / 2f) {
                LB2 += rec.getHeight();
            }
            if (rec.height == parameters.height / 2) {
                LB3 += rec.width;
            }
            if (rec.width > LB4) {
                LB4 = rec.width;
            }
        }
        int LB1 = (int) Math.ceil(totalArea / (double) parameters.height);

        if (parameters.rotationVariant) {
            return LB1;
        }
        return LB1;
//        return Math.max(LB1, LB4);
//        return Math.max(Math.max(LB1, LB4), LB2 + (int) Math.ceil(LB3 / 2));
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
        terminate:
        for (ArrayList<Rectangle> seq : new RectangleSorter(parameters.rectangles)) {
            for (float ms : new SpreadValues(seq, parameters, W)) {
                if (heuristicSolve(seq, W, (int) ms)) {
                    return true;
                }
                // Map the remaining tabu iterations against the set of tabus.
                HashMap<Integer, Integer> tabu = new HashMap<>();
                for (int i = 0; i < iter; i++) {
                    if (!(numChecks > 0)) break terminate;
                    tabu.remove(i);
                    // Seqx is the sequence with highest area utilization.
                    ArrayList<Rectangle> seqx = null;
                    int highestAreaUtil = 0;

                    Collection<Integer> solved = new HashSet<>();
                    for (ArrayList<Rectangle> rectangles : new TabuSearchGenerator(tabu.values(), 10, seq)) {
                        if (heuristicSolve(rectangles, W, (int) ms)) {
                            solved.add(rectangles.hashCode());
                        }

                        int areaUtil = parameters.height / Util.maxHeight(rectangles);
                        if (seqx == null || areaUtil > highestAreaUtil) {
                            seqx = rectangles;
                            highestAreaUtil = areaUtil;
                        }
                    }
                    if (seqx != null) {
                        if (solved.contains(seqx.hashCode())) {
                            return true;
                        }
                        tabu.put(i + 3 * parameters.rectangles.size(), seqx.hashCode());
                        seq = seqx;
                    }
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
        SpreadValues(ArrayList<Rectangle> rectangles, Parameters parameters, int W) {
            // Maximum width
            float mw = 0;
            for (Rectangle rectangle : rectangles) {
                mw = (rectangle.width > mw) ? rectangle.width : mw;
            }

            // {mh, mh + (W - mh) * 1/3, mh + (H - mh) * 2/3, H}
            spreadValues.add(mw);
            spreadValues.add(mw + (W - mw) * (1f / 3f));
            spreadValues.add(mw + (W - mw) * (2f / 3f));
            spreadValues.add((float) W);
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
        numChecks--;
        //Util.animate(animation, this);

        // Just to be sure
        resetRecs(originalSequence);

        // Make a skyline for this attempt to place all the rectangles
        ArrayListSkyline skylineDataStructure = new ArrayListSkyline(parameters.height, width, maximumSpread, parameters.rotationVariant);
        ArrayList<PositionRectangleRotationPair> minimumLocalSpaceWastePlacements = new ArrayList<>();

        // Keep track of which rectangles still need to be placed
        ArrayList<Rectangle> rectanglesNotPlacedYet = skylineDataStructure.deepCopyRectangles(originalSequence);

        // Place a rectangle every loop till every rectangle is placed
        // If it is impossible to place a rectangle, the method returns false
        while (!rectanglesNotPlacedYet.isEmpty()) {
            // Get most left x point of any segment
            int mostLeft = skylineDataStructure.getMostLeftPoint();

            // Get smallest and second smallest widths and smallest and second smallest heights
            int[] smallestRecs = skylineDataStructure.getMinWidthHeightOtherRectangles(rectanglesNotPlacedYet);

            // Before every placement, we want to keep track of what placement/which placements create(s) the least waste of space and
            // keep these in an array

            int minimumLocalSpaceWaste = Integer.MAX_VALUE;
            minimumLocalSpaceWastePlacements.clear();

            // Test if there is any perfect place to place the rectangle.
            PositionRectangleRotationPair toBePlaced = skylineDataStructure.anyOnlyFit(rectanglesNotPlacedYet, parameters.rotationVariant);

            // If there is then place it
            if (!(toBePlaced == null)) {
                placeRectangle(toBePlaced, rectanglesNotPlacedYet, skylineDataStructure);
                continue;
            }

            // Check for every rectangle-position pair the local waste
            for (SegPoint segPoint : skylineDataStructure.getCandidatePoints()) {
                for (Rectangle rectangle : rectanglesNotPlacedYet) {
                    // If the rotationsvariant is true, we want to test both rotations for every rectangle
                    for (int secondLoop = 0; secondLoop < (parameters.rotationVariant? 2 : 1); secondLoop++) {
                        if (secondLoop == 1) {
                            rectangle.rotate();

                        }
                        // Checks if the rectangle can even be placed
                        if (skylineDataStructure.doesNotMeetSpreadConstraint(rectangle, segPoint, mostLeft) || hasOverlap(rectangle, segPoint, width, originalSequence)) { // spread constraint
                            continue;
                        }
                        // Produces a list of placements which all have the lowest local space waste
                        int localSpaceWaste = skylineDataStructure.getLocalWaste(rectangle, segPoint, smallestRecs);
                        if (localSpaceWaste < minimumLocalSpaceWaste) {
                            minimumLocalSpaceWaste = localSpaceWaste;
                            minimumLocalSpaceWastePlacements.clear();
                            minimumLocalSpaceWastePlacements.add(new PositionRectangleRotationPair(rectangle, segPoint, secondLoop == 1));
                        } else if (localSpaceWaste == minimumLocalSpaceWaste) {
                            minimumLocalSpaceWastePlacements.add(new PositionRectangleRotationPair(rectangle, segPoint, secondLoop == 1));
                        }
                    }
                    // We do not want to permanently rotate this rectangle because we have not placed it yet
                    if (parameters.rotationVariant) rectangle.rotate();
                }
            }

            // If there is only one placement that has the minimum space waste, then this placement should be done
            if (minimumLocalSpaceWastePlacements.size() == 1) {
                toBePlaced = minimumLocalSpaceWastePlacements.get(0);
            } else if (minimumLocalSpaceWastePlacements.size() >= 2){
                // If there are more placements sharing the minimum weight we place the one with the highest fitness score
                // If there is a tie between those, we choose the first
                int highestFitness = 0;
                toBePlaced = minimumLocalSpaceWastePlacements.get(0);
                for (PositionRectangleRotationPair pair : minimumLocalSpaceWastePlacements) {
                    if (skylineDataStructure.getFitnessNumber(pair) > highestFitness) {
                        toBePlaced = pair;
                    }
                }
            }

            // If there is a rectangle to place we place it, otherwise we cannot place a rectangle with these parameters
            // and we return false.
            if (toBePlaced != null) {
                placeRectangle(toBePlaced, rectanglesNotPlacedYet, skylineDataStructure);
            } else {
                return false;
            }
        }
        // If we are here, that means we have placed all the rectangles and this could be a valid solution so we store
        // it (if it is the first solution or the best up to this point)
        parameters.rectangles = Util.cloneRectangleState(originalSequence);

        Solution currentSolution = new Solution(parameters, this);
//        System.out.println("solution found in heuristic solve with " + currentSolution.getWidth());
        if (globalSolution == null || currentSolution.getArea() < globalSolution.getArea()) {
            globalSolution = currentSolution.copy();
        }
        return true;
    }

    public void placeRectangle(PositionRectangleRotationPair toBePlaced, ArrayList<Rectangle> sequence, ArrayListSkyline skyline) {
        if (toBePlaced.rotated) {
            toBePlaced.rectangle.rotate();
        }

        toBePlaced.rectangle.x = toBePlaced.position.x;
        toBePlaced.rectangle.y = toBePlaced.position.y;

        if (!toBePlaced.position.start) {
            toBePlaced.rectangle.y -= toBePlaced.rectangle.height;
        }

        // Fix skyline
        skyline.adjustSkyline(toBePlaced.rectangle, toBePlaced.position);

        toBePlaced.rectangle.place(true);
        sequence.remove(toBePlaced.rectangle);

        // Make the small segments merge with bigger ones
        skyline.fixSkylineAfterPlacements(sequence, parameters.rotationVariant);
    }

    public boolean hasOverlap(Rectangle rectangle, SegPoint position, int width, ArrayList<Rectangle> originalSequence) {
        // Place rectangle on position
        rectangle.x = position.x;
        rectangle.y = position.y;


        if (!position.start) {
            rectangle.y -= rectangle.height;
        }

        // Check if rectangle crosses outerbox
        if (rectangle.y + rectangle.height > parameters.height
                || rectangle.y < 0
                || rectangle.x + rectangle.width > width) {
            return true;
        }

        for (Rectangle other : originalSequence) {
            if (other.isPlaced() && other.intersects(rectangle)) {
                return true;
            }
        }

        return false;
    }
}
