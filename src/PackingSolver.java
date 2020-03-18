public class PackingSolver {
    /**
     * @throws IllegalArgumentException if number of rectangles is not positive
     */
    public static void main(String[] args) throws IllegalArgumentException {
        UserInput ui = new UserInput(System.in);
        Parameters params = ui.getUserInput();

        // Remember the order of the rectangles for the output
        String[] inputOrder = params.rectangles.stream().map(Rectangle::getId).toArray(String[]::new);

        // Different solutions
        CompoundSolver compoundSolver = new CompoundSolver();
        compoundSolver.addSolver(new FirstFitSolver());
        compoundSolver.addSolver(new TopLeftSolver());
        compoundSolver.addSolver(new BottomUpSolver());
        compoundSolver.addSolver(new CompressionSolver());
        compoundSolver.addSolver(new ReverseFitSolver());
        compoundSolver.addSolver(new SimpleTopLeftSolver());

        Solution solution = compoundSolver.getSolution(params);
        if (Util.debug) System.err.println(solution.solvedBy);
        Output.output(solution.parameters, inputOrder);
    }

    static class Output {
        public static void output (Parameters params, String[] inputOrder) {
            if (params.heightVariant == Util.HeightSupport.FREE) {
                System.out.println("container height: free");
            } else {
                System.out.println("container height: fixed " + params.height);
            }
            System.out.println("rotations allowed: " + (params.rotationVariant ? "yes" : "no"));
            System.out.println("number of rectangles: " + params.rectangles.size() );
            for (String rectID : inputOrder) {
                Rectangle rect = params.rectangles.stream()
                        .filter(rectangle -> rectID.equals(rectangle.getId())).findAny().orElse(null);
                assert (rect != null);
                if (!rect.isRotated()) {
                    System.out.print(rect.width + " ");
                    System.out.println(rect.height);
                } else {
                    System.out.print(rect.height + " ");
                    System.out.println(rect.width);
                }
            }
            System.out.println("placement of rectangles");
            for (String rectID : inputOrder) {
                Rectangle rect = params.rectangles.stream()
                        .filter(rectangle -> rectID.equals(rectangle.getId())).findAny().orElse(null);
                assert (rect != null);
                if (params.rotationVariant) {
                    System.out.print(rect.isRotated() ? "yes " : "no ");
                }
                System.out.print(rect.x + " ");
                System.out.println(rect.y);
            }
        }
    }
}
