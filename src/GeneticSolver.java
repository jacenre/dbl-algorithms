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
        return parameters.rectangles.size() <= 500;
    }

    public GeneticSolver(AbstractSolver solver, boolean allowInputSorting) {
        super(allowInputSorting);
        this.solver = solver;
    }

    @Override
    Solution pack(Parameters parameters) {
        this.parameters = parameters.copy();

        if (!this.allowInputSorting && !parameters.rotationVariant) {
            return this.solver.pack(this.parameters);
        }

        // Array {a} holds the indexes of the rectangles list.
        // In the genetic algorithm, this will be mutated.
        int[] a = new int[parameters.rectangles.size()];
        for (int i = 0; i < a.length; i++) {
            a[i] = i;
        }

        // Create the first permutations of rectangles
        int nPermutations = 10;
        int[][] permutations = shuffle(a, nPermutations);
        Solution bestSolution = null;

        // Run 5 generations
        int nGenerations;
        // Each crossover will generate 3 new permutations per permutation
        int nRectangles = parameters.rectangles.size();
        if (nRectangles <= 6) {
            nGenerations = 10;
        } else if (nRectangles <= 15) {
            nGenerations = 100;
        } else if (nRectangles <= 100) {
            nGenerations = 50;
        } else {
            nGenerations = 10;
        }
        for (int i = 0; i < nGenerations; i++) {
            // Each permutation generates 2 new permutations
            permutations = crossover(permutations);

            // Solve for all crossovers
            Map<int[], Solution> results = new HashMap<>();
            for (int[] perm : permutations) {
                // Set the order of the rectangles as described by the permutation
                for (int j = 0; j < nRectangles; j++) {
                    this.parameters.rectangles.set(j, parameters.rectangles.get(Math.abs(perm[j])));
                    if (this.parameters.rotationVariant) {
                        Rectangle rectJ = this.parameters.rectangles.get(j);
                        if (this.parameters.heightVariant == Util.HeightSupport.FIXED
                                && rectJ.getHeight() > this.parameters.height) {
                            this.parameters.rectangles.get(j).rotate();
                        } else if (this.parameters.heightVariant == Util.HeightSupport.FREE
                                || rectJ.getWidth() < this.parameters.height) {
                            this.parameters.rectangles.get(j).rotate(perm[j] < 0);
                        }
                    }
                }

                // Calculate the solution and the solution score
                Solution pack = this.solver.pack(this.parameters);
                pack.setScore(fitnessFunction(pack));
                results.put(perm, pack);
            }

            // Sort the solutions by their score and pick the new contender
            List<Map.Entry<int[], Solution>> solutions = new ArrayList<>(results.entrySet());
            solutions.sort((r1, r2) -> Double.compare(r2.getValue().getScore(), r1.getValue().getScore()));
            int x = 0;
            Solution contenderSolution = solutions.get(x).getValue();
            while (this.parameters.heightVariant == Util.HeightSupport.FIXED
                    && contenderSolution.getHeight() > this.parameters.height && x < solutions.size()) {
                x++;
                contenderSolution = solutions.get(x).getValue();
            }

            // Compare the contender to the best solution yet
            if (x < solutions.size()) {
                if (bestSolution == null || contenderSolution.getRate() < bestSolution.getRate()) {
                    bestSolution = solutions.get(0).getValue().copy();
                }
            }

            // The permutations we will go on with are the best ones
            permutations = new int[nPermutations][permutations[0].length];
            for (int j = 0; j < nPermutations; j++) {
                permutations[j] = solutions.get(j).getKey();
            }
        }

        return bestSolution != null ? new Solution(bestSolution.parameters, this) : this.solver.pack(this.parameters);
    }

    protected double fitnessFunction(Solution solution) {
        return solution.getRate();
    }

    private int[][] crossover(int[][] permutations) {
        int[][] newPermutations = new int[permutations.length * 2][permutations[0].length];
        for (int i = 0; i < permutations.length; i++) {
            int[] perm_i = permutations[i];
            int[] perm_i1 = permutations[(i + 1) % permutations.length];
            int p = getRandomNumberInRange(1, perm_i.length - 1), q = getRandomNumberInRange(1, perm_i.length - p);

            int[] newPerm1 = actualCrossover(perm_i, p, q);
            int[] newPerm2 = actualCrossover(perm_i1, p, q);

            // Shuffle the new permutations
            newPermutations[2 * i] = shuffle(newPerm1, 2)[1];
            newPermutations[2 * i + 1] = shuffle(newPerm2, 2)[1];
        }
        return newPermutations;
    }

    private int[] actualCrossover(int[] perm_1, int p, int q) {
        int[] newPerm = new int[perm_1.length];
        System.arraycopy(perm_1, p, newPerm, 0, q);
        System.arraycopy(perm_1, 0, newPerm, q, p);
        System.arraycopy(perm_1, q + p, newPerm, q + p, newPerm.length - p - q);
        return newPerm;
    }

    private ArrayList<Util.Segment> getSegments(Solution solution) {
        // ArrayList holding all the segments we sweep over.
        ArrayList<Util.Segment> segments = new ArrayList<>();

        // Copy parameters to prevent interference with the original result
        Parameters parameters = solution.parameters.copy();

        for (Rectangle rectangle : parameters.rectangles) {
            segments.add(new Util.Segment(Util.Type.END, rectangle.y, rectangle.y + rectangle.height,
                    rectangle.x + rectangle.width, rectangle));
        }

        return segments;
    }

    /**
     * Return
     *
     * @param a array of integers
     * @param n number of permutations produced
     * @return {@code n} arrays, each a shuffle of {@code a}
     */
    int[][] shuffle(int[] a, int n) {
        int[][] permutations = new int[n][a.length];
        permutations[0] = a.clone();
        for (int i = 1; i < n; i++) {
            // Generate a new permutation of a
            int[] b = a.clone();
            for (int j = 0; j < getRandomNumberInRange(a.length / 16 + 1, a.length / 5 + 3); j++) {
                int p = getRandomNumberInRange(0, a.length - 1), q = getRandomNumberInRange(0, a.length - 1);
                if (this.allowInputSorting) {
                    // Get two random indexes and swap them
                    int temp = b[p];
                    b[p] = b[q];
                    b[q] = temp;
                }

                // Possibly rotate them
                if (parameters.rotationVariant && Math.random() < 0.4) {
                    b[p] = -1 * b[p];
                }
                if (parameters.rotationVariant && Math.random() < 0.4) {
                    b[q] = -1 * b[q];
                }
            }
            permutations[i] = b;
        }
        return permutations;
    }

    private int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
