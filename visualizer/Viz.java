import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;
import processing.core.PApplet;

import java.awt.*;
import java.util.ArrayList;

@SuppressWarnings("Duplicates")
public class Viz extends PApplet {

    private Viewport activeView = null;
    private ArrayList<Viewport> viewports = new ArrayList<>();
    private int active = 0;

    // Due to concurrency P3 width and height dont work as expected.
    private int DEFAULT_WIDTH = 1000;
    private int DEFAULT_HEIGHT = 1000;

    static boolean USER_INPUT;

    private int range = 0;

    public static void main(String[] args) {
        PApplet.main("Viz");
    }

    public void settings() {
        UserInput ui = new UserInput(System.in);
        Parameters params = ui.getUserInput();

        ArrayList<AbstractSolver> solvers = new ArrayList<>();
        solvers.add(new FirstFitSolver());
        solvers.add(new ReverseFitSolver());
        solvers.add(new SimpleTopLeftSolver());
        solvers.add(new CompoundSolver().addSolver(new FirstFitSolver()).addSolver(new ReverseFitSolver()).addSolver(new SimpleTopLeftSolver()));

        range = (int) (Math.random() * 180);

        XYChart chart = new XYChartBuilder().xAxisTitle("Height").yAxisTitle("Area").theme(Styler.ChartTheme.Matlab).width(2000).height(1000).build();
        chart.getStyler().setYAxisMin(0.0);

        for (AbstractSolver solver :
                solvers) {
            try {
                Solution solution = solver.getSolution(params.copy());
                if (solution.parameters.heightVariant.equals(Util.HeightSupport.FREE))addSeries(chart, solver, solution);
                viewports.add(new Viewport(solution));
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        if (params.copy().heightVariant.equals(Util.HeightSupport.FREE)) {
            new SwingWrapper<>(chart).displayChart();
        }

        activeView = viewports.get(0);

        size(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private void addSeries(XYChart chart, AbstractSolver solver, Solution solution) {
        int[][] chartData = solution.getChartData();
        if (chartData.length == 2) {
            if (solver.toString().startsWith("Compound")) {
                XYSeries series = chart.addSeries(solver.toString(), chartData[0], chartData[1]);
                series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
                series.setMarker(SeriesMarkers.NONE);
                int[] bestScoreX = chartData[0];
                int[] bestScoreY = chartData[1];
                for (int i = 0; i < bestScoreY.length; i++) {
                    bestScoreY[i] = solution.getArea();
                }
                chart.addSeries("Best Score", bestScoreX, bestScoreY).setMarker(SeriesMarkers.NONE);
            } else {
                XYSeries series = chart.addSeries(solver.toString(), chartData[0], chartData[1]);
                series.setMarker(SeriesMarkers.NONE);
            }
        }
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
//            this.maxSize = 800;
            this.x = 100;
            this.y = 100;
            setScale();
        }

        // Arraylist filled with all the ID's of overlapping rectangles.
        ArrayList<Rectangle> overlap = new ArrayList<>();

        void setup() {
            for (Rectangle rectangle : this.solution.parameters.rectangles) {
                smallest = (smallest > rectangle.width * rectangle.height) ? rectangle.width * rectangle.height : smallest;
                largest = (largest < rectangle.width * rectangle.height) ? rectangle.width * rectangle.height : largest;
            }
            for (Rectangle rectangle1 : this.solution.parameters.rectangles) {
                for (Rectangle rectangle2 : this.solution.parameters.rectangles) {
                    if (rectangle1 != rectangle2) {
                        if (rectangle1.intersects(rectangle2)) {
                            overlap.add(rectangle1);
                            overlap.add(rectangle2);
                        }
                    }
                }
            }
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

        int smallest = Integer.MAX_VALUE;
        int largest = 0;

        Viewport(Parameters parameters) {
            // Wrap the parameters in a solution object.
            this.solution = new Solution(parameters);
            setup();
        }

        Viewport(Solution solution) {
            this.solution = solution;
            setup();
        }

        void draw() {
            fill(0, 0, 100, 100);
            for (Rectangle rectangle :
                    solution.parameters.rectangles) {
                float hue;
                // If rectangle is overlapping draw it bright red.
                if (activeView.overlap.contains(rectangle)) {
                    stroke(0,100,100);
                    fill(0, 0, 0);
                } else {
                    hue = map((float)Math.log(rectangle.width * rectangle.height),
                            (float)Math.log(activeView.smallest), (float)Math.log(activeView.largest), range, range + 180);
                    fill(hue, 100, 100);
                    stroke(0,0,0);
                }
                float x = map(rectangle.x, 0, solution.getWidth(), 0, drawWidth);
                float y = map(rectangle.y, 0, solution.getHeight(), 0, drawHeight);
                float rectw = map(rectangle.width, 0, solution.getWidth(), 0, drawWidth);
                float recth = map(rectangle.height, 0, solution.getHeight(), 0, drawHeight);
                rect(x + this.x, y + this.y, rectw, recth);
            }
            stroke(0, 0, 0);
            fill(0, 0, 0, 0);
            rect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        }

    }

    private int oldX;
    private int oldY;
    private boolean moving = false;

    public void mousePressed() {
        oldX = mouseX;
        oldY = mouseY;
    }

    public void mouseReleased() {
        moving = false;
    }

    public void keyPressed() {
        if (keyCode == RIGHT) {
            if (active < viewports.size() - 1) {
                active++;
            } else {
                active = 0;
            }
            activeView.reset();
        } else if (keyCode == LEFT) {
            if (active >= 1) {
                active--;
            } else {
                active = viewports.size() - 1;
            }
            activeView.reset();
        } else if (keyCode == UP) {
            activeView.maxSize *= 2;
            activeView.setScale();
        } else if (keyCode == DOWN) {
            activeView.maxSize /= 2;
            activeView.setScale();
        }
        activeView = viewports.get(active);
    }

    public void mouseDragged() {
        // If you click on the strip, move it
        if (activeView.boundingBox.contains(new Point(mouseX, mouseY)) || moving) {
            moving = true;
            activeView.setX(activeView.x - oldX + mouseX);
            activeView.setY(activeView.y - oldY + mouseY);
        }
        oldX = mouseX;
        oldY = mouseY;
    }

    public void draw() {
        background(0, 0, 100);
        if (activeView != null) {
            // If the view has overlap turn the text red.
            int TEXT_X = 50;
            int TEXT_Y = 50;
            if (activeView.overlap.size() != 0) {
                fill(0, 100, 100);
                text("OVERLAP!", TEXT_X, TEXT_Y - 15);
            } else {
                fill(0, 0, 0);
            }
            text(activeView.solution.solvedBy.getClass().getSimpleName(), TEXT_X, TEXT_Y);
            text("Size = " + activeView.solution.getArea(), TEXT_X, TEXT_Y + 15);
            activeView.draw();
        }
    }

}
