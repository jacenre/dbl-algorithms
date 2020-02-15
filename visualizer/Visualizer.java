import processing.core.PApplet;

import java.util.Arrays;

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
//        compoundSolver.addSolver(new TopLeftSolver());
//        compoundSolver.addSolver(new SimpleTopLeftSolver());
//        compoundSolver.addSolver(new FreeFirstFitSolver());
        solution = compoundSolver.optimal(params);

        int width = solution.getWidth();
        int height = solution.getHeight();
        
        if (width > 1000 || height > 1000) {
            if (width > height) {
                size(1000, (height * 1000) / width);
            } else {
                size((width * 1000) / height, 1000);
            }
        } else {
            size(width, height);
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
