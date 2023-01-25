package visualizer;

import java.util.ArrayList;

import jacenre.dbla.AbstractSolver;
import jacenre.dbla.Parameters;
import jacenre.dbla.Rectangle;
import jacenre.dbla.Solution;
import processing.core.PApplet;

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
		System.setProperty("sun.java2d.uiScale", "1.0");
		System.setProperty("prism.allowhidpi", "false");
        PApplet.main(Animator.class);
    }

    @Override
	public void settings() {
        animator = this;
        size(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
	public void setup() {
        frameRate(100);
        colorMode(HSB, 360, 100, 100);
    }

    public void drawParameter(Parameters parameters, AbstractSolver solver) {
        if (stepping) {
            activeView = new Viewport(parameters.copy(), solver);
            viewports.add(activeView);
        } else if (activeView == null) {
            activeView = new Viewport(parameters, solver);
        } else {
            activeView.solution.parameters = parameters;
            activeView.solution.solvedBy = solver;
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
            this.solutionHeight = (int) this.solution.getHeight();
            this.solutionWidth = (int) this.solution.getWidth();

            if (this.solutionWidth > this.solutionHeight) {
                drawWidth = maxSize;
                drawHeight = solutionHeight * maxSize / this.solutionWidth;
            } else {
                drawWidth = solutionWidth * maxSize / solutionHeight;
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

    @Override
	public void mousePressed() {
        oldX = mouseX;
        oldY = mouseY;
    }

    @Override
	public void keyPressed() {
        if (keyCode == RIGHT) {
            currentViewIndex = (currentViewIndex + 1 + viewports.size()) % viewports.size();
            currentView().reset();
        } else if (keyCode == LEFT) {
            currentViewIndex = (currentViewIndex - 1 + viewports.size()) % viewports.size();
            currentView().reset();
        }
        currentView().setScale();
    }

    int TEXT_X = 50;
    int TEXT_Y = 50;
    boolean stepping = false; // Turn on or off that you manually click through each view
    boolean showNext = true;
    int currentViewIndex = 0;

    private Viewport currentView() {
        return stepping ? viewports.get(currentViewIndex) : activeView;
    }

    @Override
	public void draw() {
        try {
            if (Animator.getInstance() != null) {
                Viewport view = currentView();
                background(0,0,100);
                if (view != null) {
                    fill(0, 0, 0);
                    text(view.solution.solvedBy.getClass().getSimpleName(), TEXT_X, TEXT_Y);
                    text("Size = " + view.solution.getArea(), TEXT_X, TEXT_Y + 15);
                    view.draw();
                }
                delay(stepping ? 10 : 100);
            }
        } catch (Exception e) {
            // ignore
        }
    }
}
