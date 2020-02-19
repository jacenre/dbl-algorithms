import processing.core.PApplet;

import java.awt.*;
import java.util.ArrayList;

@SuppressWarnings("Duplicates")
public class Viz extends PApplet {

    Viewport activeView = null;
    ArrayList<Viewport> viewports = new ArrayList<>();
    int active = 0;

    // Due to concurrency P3 width and height dont work as expected.
    int DEFAULT_WIDTH = 1000;
    int DEFAULT_HEIGHT = 1000;

    static boolean USER_INPUT;

    public static void main(String[] args) {
        PApplet.main("Viz");
    }

    public void settings() {
        UserInput ui = new UserInput(System.in);
        Parameters params = ui.getUserInput();

        ArrayList<AbstractSolver> solvers = new ArrayList<>();
        solvers.add(new FirstFitSolver());
        solvers.add(new TopLeftSolver());
        solvers.add(new CompressionSolver());
        solvers.add(new ReverseFitSolver());
        solvers.add(new SimpleTopLeftSolver());
        solvers.add(new FreeFirstFitSolver());

        for (AbstractSolver solver :
                solvers) {
            try {
                Solution solution = solver.solve(params.copy());
                viewports.add(new Viewport(solution));
            } catch (IllegalArgumentException e) {
                System.out.println(e);
            }
        }

        activeView = viewports.get(0);

        size(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public void setup() {
        colorMode(HSB, 360, 100, 100);
    }

    public void drawParameter(Parameters parameters) {
        activeView = new Viewport(parameters);
    }

    /**
     * Object responsible for drawing parameters as rectangles
     */
    class Viewport {

        // Max size of either the width or height.
        int maxSize = 800;

        int x = 100;

        public void setX(int x) {
            this.x = x;
            this.boundingBox.x = x;
        }

        int y = 100;

        public void setY(int y) {
            this.y = y;
            this.boundingBox.y = y;
        }

        int solutionWidth;
        int solutionHeight;

        Rectangle boundingBox;

        // Scale factor
        int drawWidth;
        int drawHeight;

        Solution solution;

        void reset() {
            this.maxSize = 800;
            this.x = 100;
            this.y = 100;
            setScale();
        }

        void setScale() {
            // update sizes
            this.solutionHeight = this.solution.getHeight();
            this.solutionWidth = this.solution.getWidth();

            if (this.solutionWidth > this.solutionHeight) {
                drawWidth = maxSize;
                drawHeight = (solutionHeight * maxSize) / this.solutionWidth;
            } else {
                drawWidth = (solutionWidth * maxSize) / solutionHeight;
                drawHeight = maxSize;
            }

            this.x = (DEFAULT_WIDTH - drawWidth) / 2;
            this.y = (DEFAULT_HEIGHT - drawHeight) / 2;

            boundingBox = new Rectangle(this.x, this.y, drawWidth, drawHeight);
        }

        Viewport(Parameters parameters) {
            // Wrap the parameters in a solution object.
            this.solution = new Solution(parameters);
            setScale();
        }

        Viewport(Solution solution) {
            this.solution = solution;
            setScale();
        }

        void draw() {
            fill(0,0,100,100);
            for (Rectangle rectangle :
                    solution.parameters.rectangles) {
                fill((rectangle.width*rectangle.height*10)%360, (rectangle.width*rectangle.height*10)%360,100);
                float x = map(rectangle.x, 0, solution.getWidth(), 0, drawWidth);
                float y = map(rectangle.y, 0, solution.getHeight(), 0, drawHeight);
                float rectw = map(rectangle.width, 0, solution.getWidth(), 0, drawWidth);
                float recth = map(rectangle.height, 0, solution.getHeight(), 0, drawHeight);
                rect(x + this.x, y + this.y, rectw, recth);
            }
            stroke(0,0,0);
            fill(0, 0, 0, 0);
            rect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        }

    }

    int oldX;
    int oldY;

    public void mousePressed() {
        oldX = mouseX;
        oldY = mouseY;
    }

    public void keyPressed() {
        if (keyCode == RIGHT) {
            if (active < viewports.size() - 1) {
                active++;
            } else {
                active = 0;
            }
        } else if (keyCode == LEFT) {
            if (active >= 1) {
                active--;
            } else {
                active = viewports.size() - 1;
            }
        }
        activeView = viewports.get(active);
        activeView.reset();
    }

    public void mouseDragged() {
        // If you click on the strip, move it
        if (activeView.boundingBox.contains(new Point(mouseX, mouseY))) {
            activeView.setX(activeView.x - oldX + mouseX);
            activeView.setY(activeView.y - oldY + mouseY);
        }
        oldX = mouseX;
        oldY = mouseY;
    }

    int TEXT_X = 50;
    int TEXT_Y = 50;

    public void draw() {
        background(0,0,100);
        if (activeView != null) {
            fill(0,0,0);
            text(activeView.solution.solvedBy.getClass().getSimpleName(),TEXT_X,TEXT_Y);
            text("Size = " + activeView.solution.getArea(), TEXT_X,TEXT_Y + 15);
            activeView.draw();
        }
    }

}
