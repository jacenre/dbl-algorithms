import org.junit.jupiter.params.shadow.com.univocity.parsers.common.record.Record;

import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;



/**
 * Solver algorithm using the Skyline heuristic
 *
 * @see <a href="https://www-sciencedirect-com.dianus.libr.tue.nl/science/article/pii/S0377221711005510">source</a>
 */
public class SkylineSolver extends AbstractSolver {

    Solution globalSolution;
    int globalHeight;

    // Algorithm 2 in the paper
    @Override
    Solution pack(Parameters parameters) {
        int globalHeight = parameters.height;
        int lowerBound = getLowerBound(parameters);
        int upperBound = (int) (lowerBound * 1.1);
        int iter = 1;
        boolean upperBoundFound = false;

        while(/*time limit not reached and */ lowerBound != upperBound) {
            int tempLowerBound = lowerBound;
            while (tempLowerBound < upperBound) {
                int width = ((tempLowerBound + upperBound) / 2);
                if (solve(width, iter)) {
                    /* record this solution */
                    upperBound = width;
                    upperBoundFound = true;
                } else {
                    tempLowerBound = width + 1;
                }
            }
            if (upperBoundFound == false)
                upperBound = (int)(upperBound * 1.1);
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
        int LB1 = (int)Math.ceil(totalArea/(double)parameters.height);

        if (parameters.rotationVariant) {
            return LB1;
        }

        return Math.max(LB1, LB2 + (int)Math.ceil(LB3 / 2));
    }

    /**
     * Handles a Tabu search for a good sequence of rectangles and good maximum spread.
     *
     * @param W The width that has been supplied by the pack method
     * @param iter The iterations variant that has also been supplied by the pack method
     * @returns a boolean signalling if a solution could be found with the given attributes
     */
    boolean solve(int W, int iter) {
        return false;
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
     * @param sequence The sequence of rectangles, which can be very different according to different sorting and
     *                 the random permutations made by the tabu search algorithm
     * @param width The given width to which to adhere
     * @param maximumSpread The given maximumSpread to which to adhere
     * @return true or false whether the heuristic was able to pack all the rectangles given the restrictions
     */
    boolean heuristicPacking(ArrayList<Rectangle> sequence, int width, int maximumSpread) {
        // Test all the candidate positions - rectangle combos
        ArrayListSkyline skyline = new ArrayListSkyline(globalHeight, width, maximumSpread);
        ArrayList<PositionRectanglePair> minimumLocalSpaceWasteRectangles = new ArrayList<>();

        while (!sequence.isEmpty()) {
            int minimumLocalSpaceWaste = Integer.MAX_VALUE;
            minimumLocalSpaceWasteRectangles.clear();
            PositionRectanglePair toBePlaced = null;

           // if (skyline.anyOnlyFit(sequence)) {
           //     continue;
           // }
            for (SegPoint segPoint : skyline.getCandidatePoints()) {
                for (Rectangle rectangle : sequence) {
                    if (skyline.testSpreadConstraint(rectangle, segPoint)) { // spread constraint
                        continue;
                    }
                    int localSpaceWaste = skyline.getLocalWaste(rectangle, segPoint, sequence);
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
                    if (skyline.getFitnessNumber(pair.rectangle, pair.position) > highestFitness) {
                        toBePlaced = pair;
                    }
                }
            }
            if (toBePlaced != null) {
                skyline.addRectangle(toBePlaced.rectangle, toBePlaced.position);
            } else {
                return false;
            }
        }
        return true;
    }

}
