import java.util.ArrayList;
import java.util.Random;

/**
 * Utility class to apply to any solver to iteratively mutate
 * the input order and/or rotation of the rectangles.
 * For strip-packing only!
 */
public class GeneticSolver {
    private AbstractSolver solver;
    private int maxHeight;
    private boolean rotationAllowed;
    private int[][] permutations;

    public GeneticSolver(AbstractSolver solver, Parameters parameters, int maxHeight, boolean rotationAllowed) {
        this.solver = solver;
        this.maxHeight = maxHeight;
        this.rotationAllowed = rotationAllowed;
        int[] a = new int[parameters.rectangles.size()];
        for (int i = 1; i <= a.length; i++) {
            a[i] = i;
        }
        this.permutations = shuffle(a, 10);
    }

    protected double fitnessFunction(Solution solution) {
        int areaWidth = solution.getWidth();

        // Get the rectangle that we can most easily make less wide
        // Do this by getting largest area that can be filled by a single rectangle
        // that is below the current rectangles
        ArrayList<Util.Segment> segments = getSegments(solution);

        // We sweep from right to left
        segments.sort((o1, o2) -> {
            // If same X, then from bottom to top
            if (o1.x == o2.x) {
                return o2.yEnd - o1.yEnd;
            }
            // Else sort on x
            return o2.x - o1.x;
        });

        // Calculate the reusableTrimLoss by the maximal rectangle that can be made
        // from the remaining space to the bottom right of the box
        int y = segments.get(0).yEnd;
        double reusableTrimLoss = 0;
        for (Util.Segment seg: segments) {
            if (seg.yEnd >= y) {
                reusableTrimLoss = Math.max(reusableTrimLoss, (maxHeight - y) * (areaWidth - seg.x));
                y = seg.yEnd;
            }
        }

        double boxArea = (areaWidth * maxHeight);
        return -0.8 * boxArea + reusableTrimLoss / boxArea;
    }

    private ArrayList<Util.Segment> getSegments(Solution solution) {
        // ArrayList holding all the segments we sweep over.
        ArrayList<Util.Segment> segments = new ArrayList<>();

        // Copy parameters to prevent interference with the original result
        Parameters parameters = solution.parameters.copy();

        for (Rectangle rectangle : parameters.rectangles) {
            segments.add(new Util.Segment(Util.Type.END, rectangle.y, rectangle.y + rectangle.height, rectangle.x + rectangle.width, rectangle));
        }

        return segments;
    }

    /**
     * Return
     * @param a  array of integers
     * @param n  number of permutations
     * @return  {@code n} arrays, each a shuffle of {@code a}
     */
    int[][] shuffle(int[] a, int n)
    {
        int[][] permutations = new int[n][a.length];
        for (int i = 0; i < n; i++) {
            // Generate a new permutation of a
            int[] b = a.clone();
            for (int j = 0; j < getRandomNumberInRange(1, 6); j++) {
                // Get two random indexes and swap them
                int p = getRandomNumberInRange(0, a.length), q = getRandomNumberInRange(0, a.length);
                int temp = a[p];
                a[p] = a[q];
                a[q] = temp;

                // Possibly rotate them
                if (rotationAllowed && Math.random() < 0.15) {
                    a[p] = -1 * a[p];
                }
                if (rotationAllowed && Math.random() < 0.15) {
                    a[q] = -1 * a[q];
                }
            }
            permutations[i] = b;
        }
        return permutations;
    }

    private int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.nextInt(max - min) + min;
    }
}
