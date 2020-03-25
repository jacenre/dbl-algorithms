import java.util.*;

/**
 * Utility class to apply to any solver to iteratively mutate
 * the input order and/or rotation of the rectangles.
 * For strip-packing only! (fitness function depends on it)
 */
public class GeneticSolver extends AbstractSolver {
    private AbstractSolver solver;
    private Parameters parameters;

    @Override
    Set<Util.HeightSupport> getHeightSupport() {
        return new HashSet<>(Collections.singletonList(Util.HeightSupport.FIXED));
    }

    @Override
    public boolean canSolveParameters(Parameters parameters) {
        boolean superResult = super.canSolveParameters(parameters);
        if (!superResult) return false;
        if (parameters.rectangles.size() > 500 && (
                parameters.heightVariant == Util.HeightSupport.FREE || parameters.freeHeightUtil)) return false;
        return parameters.rectangles.size() <= 800;
    }

    public GeneticSolver(AbstractSolver solver, boolean allowInputSorting) {
        super(allowInputSorting);
        this.solver = solver;
    }

    @Override
    Solution pack(Parameters parameters) {
        this.parameters = parameters.copy();

        if (!this.allowInputSorting && !parameters.rotationVariant) {
            return this.solver.pack(parameters);
        }

        // Array {a} holds the indexes of the rectangles list.
        // In the genetic algorithm, this will be mutated.
        int[] a = new int[parameters.rectangles.size()];
        for (int i = 0; i < a.length; i++) {
            a[i] = i;
        }

        // Create the first permutations of rectangles
        int[][] permutations = shuffle(a, 5);
        Solution bestSolution = null;

        // Run 5 generations
        for (int i = 0; i < 5; i++) {
            // Each permutation generates {crossoverN} new permutations
            int crossoverN = 3;
            permutations = crossover(permutations, crossoverN);

            // Solve for all crossovers
            Map<int[], Solution> results = new HashMap<>();
            for (int[] perm : permutations) {
                // Set the order of the rectangles as described by the permutation
                for (int j = 0; j < parameters.rectangles.size(); j++) {
                    boolean rotated = perm[j] < 0;
                    this.parameters.rectangles.set(j, parameters.rectangles.get(Math.abs(perm[j])));
                    this.parameters.rectangles.get(j).rotate(perm[j] < 0);
                }

                // Calculate the solution and the solution score
                Solution pack = this.solver.pack(this.parameters);
                pack.setScore(fitnessFunction(pack));
                results.put(perm, pack);
            }

            // Sort the solutions by their score
            List<Map.Entry<int[], Solution>> solutions = new ArrayList<>(results.entrySet());
            solutions.sort((r1, r2) -> Double.compare(r2.getValue().getScore(), r1.getValue().getScore()));
            if (bestSolution == null || solutions.get(0).getValue().getScore() > bestSolution.getScore()) {
                bestSolution = solutions.get(0).getValue();
            }

            // The permutations we will go on with are the best ones
            for (int j = 0; j < solutions.size() / crossoverN; j++) {
                permutations[j] = solutions.get(j).getKey();
            }
        }

        System.err.println((new Solution(parameters)).getRate());

        return new Solution(parameters, this);
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
                reusableTrimLoss = Math.max(reusableTrimLoss, (parameters.height - y) * (areaWidth - seg.x));
                y = seg.yEnd;
            }
        }

        double boxArea = (areaWidth * parameters.height);
        return reusableTrimLoss / boxArea;
    }

    private int[][] crossover(int[][] permutations, int n) {
        int[][] newPermutations = new int[permutations.length * n][permutations[0].length];
        for (int i = 0; i < permutations.length; i++) {
            newPermutations[n*i] = permutations[i];
            for (int j = 1; j < n; j++) {
                newPermutations[n*i + j] = shuffle(permutations[i], 1)[0];
            }
        }
        return newPermutations;
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
     * @param n  number of permutations produced
     * @return  {@code n} arrays, each a shuffle of {@code a}
     */
    int[][] shuffle(int[] a, int n) {
        int[][] permutations = new int[n][a.length];
        for (int i = 0; i < n; i++) {
            // Generate a new permutation of a
            int[] b = a.clone();
            for (int j = 0; j < getRandomNumberInRange(1, 4); j++) {
                int p = getRandomNumberInRange(0, a.length), q = getRandomNumberInRange(0, a.length);
                if (this.allowInputSorting) {
                    // Get two random indexes and swap them
                    int temp = b[p];
                    b[p] = b[q];
                    b[q] = temp;
                }

                // Possibly rotate them
                if (parameters.rotationVariant && Math.random() < 0.2) {
                    b[p] = -1 * b[p];
                }
                if (parameters.rotationVariant && Math.random() < 0.2) {
                    b[q] = -1 * b[q];
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
