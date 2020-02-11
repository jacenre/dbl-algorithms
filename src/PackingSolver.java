import java.util.Arrays;

public class PackingSolver {
    /**
     * @throws IllegalArgumentException if number of rectangles is not positive
     */
    public static void main(String[] args) throws IllegalArgumentException {
        UserInput ui = new UserInput(System.in);
        Parameters params = ui.getUserInput();
        AbstractSolver problemSolver = new FirstFitSolver();
        String[] inputOrder = params.rectangles.stream().map(Rectangle::getId).toArray(String[]::new);
        int[] solution = problemSolver.optimal(params);
        Output.output(params, inputOrder);
        Output.outputVisual(params, inputOrder, solution);
    }

    static class Output {
        public static void output (Parameters params, String[] inputOrder) {
            System.out.println("container height: " + params.heightVariant +
                    (params.height != Integer.MAX_VALUE ? " " + params.height : ""));
            System.out.println("rotations allowed: " + (params.rotationVariant ? "yes" : "no"));
            System.out.println("number of rectangles: " + params.rectangles.size() );
            for (String rectID : inputOrder) {
                Rectangle rect = params.rectangles.stream()
                        .filter(rectangle -> rectID.equals(rectangle.getId())).findAny().orElse(null);
                assert (rect != null);
                System.out.print(rect.x + " ");
                System.out.println(rect.y);
            }
        }

        public static void outputVisual (Parameters params, String[] inputOrder, int[] solution) {
            String[][] solutionGrid = new String[solution[0]][solution[1]];
            for (String[] row : solutionGrid) {
                Arrays.fill(row, "-");
            }
            for (String rectID : inputOrder) {
                Rectangle rect = params.rectangles.stream()
                        .filter(rectangle -> rectID.equals(rectangle.getId())).findAny().orElse(null);
                assert (rect != null);
                for (int i = rect.y; i < rect.y + rect.getHeight(); i++) {
                    for (int j = rect.x; j < rect.x + rect.getWidth(); j++) {
                        assert(solutionGrid[solution[0] - 1 - i][j].equals("-"));
                        solutionGrid[solution[0] - 1 - i][j] = "*";
                    }
                }
            }
            for (String[] row : solutionGrid) {
                System.out.println();
                for (String cell : row) {
                    System.out.print(cell + " ");
                }
            }
        }
    }
}
