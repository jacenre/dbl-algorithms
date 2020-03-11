import java.util.ArrayList;

/**
 * Global Util class for commonly used function and constants.
 */
public class Util {

    /**
     * Used for checking height variants.
     * <p>
     *     Here {@code FIXED} represents the fixed height problem and {@code FREE} represents the free height problem.
     *     Mostly used in checking if a parameter is applicable to a certain solver, like {@link AbstractSolver#canSolveParameters(Parameters)}
     *     @see <a href="https://canvas.tue.nl/files/1978093/download?download_frd=1"> Height Formats</a>
     * </p>
     */
    public enum HeightSupport {
        FIXED,
        FREE
    }

    /**
     * Creates a deep copy of a {@link Rectangle} ArrayList with the same {@link Rectangle#getId()} for each rectangle.
     *
     * @param rects Input array to copy
     * @return A deep copy ArrayList.
     */
    public static ArrayList<Rectangle> cloneRectangleState(ArrayList<Rectangle> rects) {
        ArrayList<Rectangle> rectangles = new ArrayList<>();
        for (Rectangle rect :
                rects) {
            rectangles.add(rect.copy());
        }
        return rectangles;
    }



    /**
     * Used for animating the current parameters.
     *
     * @param parameters Parameters to animate.
     * @param solver     Solver that called the animation.
     *                   TODO COMMENT OUT BEFORE HANDING IN.
     */
    public static void animate(Parameters parameters, AbstractSolver solver) {
        if (animate && Animator.getInstance() != null){
            Animator.getInstance().draw();
            Animator.getInstance().drawParameter(parameters, solver);
        }
    }

    // Global switch if the solution should be animated.
    private static boolean animate = false;

    public static void animate() {
        if (animate && Animator.getInstance() != null) {
            Animator.getInstance().draw();
        }
    }

    /**
     * Times how long it takes to find a solution, used for debugging and test cases.
     *
     * @param parameters the Parameters to be packed
     * @param solver     the Solver to do the packing
     */
    public static boolean timedPacker(Parameters parameters, AbstractSolver solver) {
        long startTime = System.nanoTime();

        Solution solution = solver.getSolution(parameters);

        long endTime = System.nanoTime();

        //divide by 1000000 to get milliseconds
        long duration = (endTime - startTime) / 1000000;
        System.out.println("Packing took " + duration + "ms");

        if (duration > 30000) {
            System.err.println("Packing took longer than 30 seconds");
            return false;
        }

        return isValidSolution(solution, true);
    }

    /**
     * Sweepline algorithm to check if the solution has overlap.
     *
     * @param solution the Solution to check for overlap
     * @return boolean value representing if there is overlap
     */
    public static boolean sweepline(Solution solution) {
        // ArrayList holding all the segments we sweep over.
        ArrayList<Segment> segments = new ArrayList<>();
        ArrayList<Segment> active = new ArrayList<>();

        // Copy parameters to prevent interference with the original result
        Parameters parameters = solution.parameters.copy();

        for (Rectangle rectangle : parameters.rectangles) {
            segments.add(new Segment(Type.START, rectangle.y, rectangle.y + rectangle.height, rectangle.x, rectangle));
            segments.add(new Segment(Type.END, rectangle.y, rectangle.y + rectangle.height, rectangle.x + rectangle.width, rectangle));
        }

        // We sweep from left to right
        segments.sort((o1, o2) -> {
            if (o1.x == o2.x) {
                // END has higher priority then START
                if (o1.type == Type.END) return -1;
                if (o2.type == Type.END) return 1;
            }
            // Else sort on x
            return o1.x - o2.x;
        });

        // Start sweep
        for (Segment segment :
                segments) {
            // Left side of rectangle
            if (segment.type == Type.START) {
                // If the interval is already in the tree there is overlap.
                if (active.contains(segment)) return true;

                for (Segment segment1 :
                        active) {
                    if ((segment.yStart > segment1.yStart && segment.yStart < segment1.yEnd)
                            || (segment.yEnd > segment1.yStart && segment.yEnd < segment1.yEnd)) {
                        // Overlap
                        return true;
                    }
                }
                active.add(segment);
            }
            // Right side of rectangle
            else if (segment.type == Type.END) {
                active.remove(segment);
            }
        }
        return false;
    }

    // Representing a line Segment for the sweep
    static class Segment {
        // Left or right side of the rectangle
        Type type;

        // y values
        int yStart;
        int yEnd;
        int x;

        // What rectangle this segment is representing.
        Rectangle rectangle;

        Segment(Type type, int yStart, int yEnd, int x, Rectangle rectangle) {
            this.type = type;
            this.yStart = yStart;
            this.yEnd = yEnd;
            this.x = x;
            this.rectangle = rectangle;
        }

        @Override
        public String toString() {
            return this.type + ", " + this.yStart + " : " + this.yEnd + ", " + this.x + "  ";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj.getClass() != this.getClass()) return false;
            Segment seg = (Segment) obj;
            return (seg.yStart == this.yStart && seg.yEnd == this.yEnd);
        }
    }

    enum Type {
        START,
        END
    }

    // Global debug boolean
    static boolean debug = false;

    /**
     * Check if the solution found by the solver is valid.
     *
     * @param solution the solution to be checked
     * @return a boolean that is true if the solution is valid.
     */
    public static boolean isValidSolution(Solution solution) {
        Double rate;
        rate = solution.getRate();

        // Test report
        if (debug) {
            System.out.println(solution);
        }


        if (sweepline(solution)) {
            if (debug) System.err.println("There are overlapping rectangles");
            return false;
        }

        for (Rectangle rectangle :
                solution.parameters.rectangles) {
            if (rectangle.x < 0 || rectangle.y < 0) {
                if (debug) System.err.println("Negative coordinates found");
                return false;
            }
        }

        if (solution.parameters.heightVariant == Util.HeightSupport.FIXED) {
            for (Rectangle rectangle : solution.parameters.rectangles) {
                if (rectangle.y + rectangle.height > solution.parameters.height) {
                    if (debug) System.err.println("The height limit is not maintained, " +
                            (rectangle.y + rectangle.height) + " went over the maximum height of " +
                            solution.parameters.height);
                    return false;
                }
            }
        }

        if (rate < 1) {
            if (debug) System.err.println("Impossible result.");
            return false;
        }

        return true;
    }

    /**
     * Check if the solution found by the solver is valid without debug parameter.
     * <p>
     *     Calling this with {@code debug == false} makes it useful as a utility to quickly check if the solution is
     *     valid and should be reported. Due to the speed of {@link #sweepline(Solution)} it is viable to use this
     *     at runtime.
     * </p>
     *
     * @param solution the solution to be checked
     * @param debug boolean representing if there should be debug output
     * @return a boolean that is true if the solution is valid.
     */
    public static boolean isValidSolution(Solution solution, boolean debug) {
        boolean oldDebug = Util.debug;
        Util.debug = debug;
        boolean valid = isValidSolution(solution);
        Util.debug = oldDebug;
        return valid;
    }

    /**
     * Test to see if any of the rectangles in the list overlap.
     * <p>
     *     Running time is O(n^2), the running time of {@link #sweepline(Solution)} is O(n log n).
     *     Therefore use the sweepline instead of this.
     * </p>
     *
     * @deprecated since {@link #sweepline(Solution)} is faster
     * @return a boolean that is true if there is overlap
     */
    @Deprecated
    public static boolean hasOverlapping(ArrayList<Rectangle> rectangles) {
        for (Rectangle rectangle1 :
                rectangles) {
            for (Rectangle rectangle2 :
                    rectangles) {
                if (!rectangle1.equals(rectangle2)) {
                    if (rectangle1.intersects(rectangle2)) {
                        System.out.println(rectangle1.getId());
                        System.out.println(rectangle2.getId());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns the height of the largest rectangle in the parameters rectangle arrays.
     *
     * @param parameters the Parameters in which to search
     * @return the height of the largest rectangle
     */
    static int largestRect(Parameters parameters) {
        int height = 0;
        for (Rectangle rectangle :
                parameters.rectangles) {
            height = Math.max(rectangle.height, height);
            if (parameters.rotationVariant) {
                height = Math.max(rectangle.width, height);
            }
        }
        return height;
    }

    /**
     * Returns the sum of all the heights in the parameters rectangle arrays.
     *
     * @param parameters the Parameters for which to sum
     * @return the sum of all the heights
     */
    static int sumHeight(Parameters parameters) {
        int sum = 0;
        for (Rectangle rectangle :
                parameters.rectangles) {
            if (parameters.rotationVariant) {
                sum += Math.max(rectangle.height, rectangle.width);
            } else {
                sum += rectangle.height;
            }
        }
        return sum;
    }

}