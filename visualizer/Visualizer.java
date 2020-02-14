import processing.core.PApplet;

/**
 * Visualizer using the P3 library
 */
public class Visualizer extends PApplet {

    static Parameters params;
    static CompoundSolver compoundSolver;

    Solution solution;

    public static void main(String[] args) {
        PApplet.main("Visualizer");
    }

    public void settings() {
        UserInput ui = new UserInput(System.in);
        params = ui.getUserInput();

        // Different solutions
        compoundSolver = new CompoundSolver();
        compoundSolver.addSolver(new FirstFitSolver());
        compoundSolver.addSolver(new TopLeftSolver());
        solution = compoundSolver.optimal(params);

        if (solution.width > 1000 || solution.height > 1000) {
            if (solution.width > solution.height) {
                size(1000, (solution.height * 1000) / solution.width);
            } else {
                size((solution.width * 1000) / solution.height, 1000);
            }
        } else {
            size(solution.width, solution.height);
        }
    }

    public void setup() {
        colorMode(HSB, 360, 100, 100);
    }

    public void draw() {
        noLoop();
        for (Rectangle rectangle :
                solution.parameters.rectangles) {
            rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
    }
}
