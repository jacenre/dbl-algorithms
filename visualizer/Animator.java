import processing.core.PApplet;

import java.awt.*;
import java.util.ArrayList;

/**
 * Animator using the P3 library
 */
@SuppressWarnings("Duplicates")
public class Animator extends PApplet {

    static Animator animator = null;

    public static Animator getInstance() {
        if (animator == null) {
            Animator.main(new String[]{});
        }
        return animator;
    }

    Viewport activeView;
    ArrayList<Viewport> viewports = new ArrayList<>();
    int active = 0;

    // Due to concurrency P3 width and height dont work as expected.
    int DEFAULT_WIDTH = 1000;
    int DEFAULT_HEIGHT = 1000;

    static boolean USER_INPUT;

    public static void main(String[] args) {
        PApplet.main("Animator");
    }

    public void settings() {
        animator = this;
        size(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public void setup() {
        colorMode(HSB, 360, 100, 100);
    }

    public void drawParameter(Parameters parameters, AbstractSolver solver) {
        if (activeView == null) {
            activeView = new Viewport(parameters, solver);
        } else {
            activeView.solution.parameters = parameters;
        }
        activeView.setScale();
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

        Viewport(Parameters parameters, AbstractSolver solver) {
            // Wrap the parameters in a solution object.
            this.solution = new Solution(parameters, solver);
            setScale();
        }

        Viewport(Solution solution) {
            this.solution = solution;
            setScale();
        }

        void draw() {
            fill(0, 0, 100, 100);
            for (Rectangle rectangle :
                    solution.parameters.rectangles) {
                if (rectangle.isPlaced()) {
                    fill(map(rectangle.x, 0, solution.getWidth(), 0, 360),
                            map(rectangle.y, 0, solution.getHeight(), 50, 100), 100);
                    float x = map(rectangle.x, 0, solution.getWidth(), 0, drawWidth);
                    float y = map(rectangle.y, 0, solution.getHeight(), 0, drawHeight);
                    float rectw = map(rectangle.width, 0, solution.getWidth(), 0, drawWidth);
                    float recth = map(rectangle.height, 0, solution.getHeight(), 0, drawHeight);
                    rect(x + this.x, y + this.y, rectw, recth);
                }
            }
            stroke(0, 0, 0);
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

    int TEXT_X = 50;
    int TEXT_Y = 50;

    public void draw() {
        try {
            if (Animator.getInstance() != null) {
                background(0,0,100);
                if (activeView != null) {
                    fill(0, 0, 0);
                    text(activeView.solution.solvedBy.getClass().getSimpleName(), TEXT_X, TEXT_Y);
                    text("Size = " + activeView.solution.getArea(), TEXT_X, TEXT_Y + 15);
                    activeView.draw();
                }
                delay(100);
            }
        } catch (Exception e) {
            // ignore
        }
    }
}
