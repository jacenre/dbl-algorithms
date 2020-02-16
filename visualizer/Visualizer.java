import processing.core.PApplet;

import java.util.Arrays;

/**
 * Visualizer using the P3 library
 */
public class Visualizer extends PApplet {

    static Parameters params;
    static CompoundSolver compoundSolver;

    static int WINDOW_SIZE = 1500;

    Solution solution;

    public static void main(String[] args) {
        PApplet.main("Visualizer");
    }

    public void settings() {
        UserInput ui = new UserInput(System.in);
        params = ui.getUserInput();

        // Different solutions
//        compoundSolver = new CompoundSolver();
        FirstFitSolver firstFitSolver = new FirstFitSolver();
        TopLeftSolver topLeftSolver = new TopLeftSolver();
//        compoundSolver.addSolver(new FreeFirstFitSolver());
        solution = firstFitSolver.optimal(params);
//        solution = topLeftSolver.optimal(solution.parameters);

        System.out.println(solution.getArea());

        int width = solution.getWidth();
        int height = solution.getHeight();

        if (width > height) {
            size(WINDOW_SIZE, (height * WINDOW_SIZE) / width);
        } else {
            size((width * WINDOW_SIZE) / height, WINDOW_SIZE);
        }
    }

    public void drawParameters(Parameters parameters) {
        for (Rectangle rectangle :
                parameters.rectangles) {
            rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
    }

    public void setup() {
        colorMode(HSB, 360, 100, 100);
    }

    public void draw() {
        background(0,0,0);
        noLoop();
        for (Rectangle rectangle :
                solution.parameters.rectangles) {
            float x = map(rectangle.x, 0, solution.getWidth(), 0, width);
            float y = map(rectangle.y, 0, solution.getHeight(), 0, height);
            float rectw = map(rectangle.width, 0, solution.getWidth(), 0, width);
            float recth = map(rectangle.height, 0, solution.getHeight(), 0, height);
            rect(x, y, rectw, recth);
        }
    }
}
