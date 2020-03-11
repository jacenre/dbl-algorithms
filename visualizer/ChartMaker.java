import org.junit.jupiter.api.DynamicTest;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class ChartMaker {
    public void charts() throws IOException {
        ArrayList<AbstractSolver> solvers = new ArrayList<>();
        solvers.add(new FirstFitSolver());
        solvers.add(new TopLeftSolver());
        solvers.add(new CompressionSolver());
        solvers.add(new ReverseFitSolver());
        solvers.add(new SimpleTopLeftSolver());
        solvers.add(new BottomUpSolver());

        //String path = "./test/input/Non-perfect fit/Bortfeldt, 2006";
        //String path = "./test/input/ChartSelection";
        String path = "./test/input/selection";

        File folder = new File(path);
        File[] files = folder.listFiles();
        assert files != null;

        // Filter files on .in extension.
        files = Arrays.stream(files)
                .filter(File::isFile)
                .filter(file -> file.getName().substring(file.getName().lastIndexOf(".") + 1).equals("in"))
                .toArray(File[]::new);

        XYChart chart = new XYChartBuilder().xAxisTitle("Height").yAxisTitle("Area").theme(Styler.ChartTheme.Matlab).width(1600).height(900).build();
        chart.getStyler().setYAxisMin(0.0);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSE);

        double[] xData;
        double[] yData;
        int size;

        // Show the graph
        final SwingWrapper<XYChart> sw = new SwingWrapper<>(chart);
        sw.displayChart();
        double[] globalDataX = new double[files.length];
        double[] globalDataY = new double[files.length];

        // Fill in the actual data
        try {
            for (AbstractSolver solver : solvers) {

                for (int j = 0; j < files.length; j++) {
                    globalDataX[j] = j;
                    globalDataY[j] = 0;
                }

                size = 1;
                xData = new double[size];
                yData = new double[size];

                xData[0] = 0;
                yData[0] = 0;

                chart.addSeries(solver.toString(), xData, yData);
                sw.repaintChart();


                for (File file : files) {
                    // xData fill up
                    xData = new double[size];
                    xData = copyData(xData, globalDataX);

                    // yData fill up
                    yData = new double[size];
                    yData = copyData(yData, globalDataY);

                    // yData last entry
                    try {
                        Parameters params = (new UserInput(new FileInputStream(file))).getUserInput();
                        Solution solution = solver.getSolution(params.copy());
                        yData[size - 1] = (double) solution.getArea();
                        globalDataY[size - 1] = (double) solution.getArea();
                    } catch (Exception e) {
                        // ignore, y will be 0
                    }

                    chart.updateXYSeries(solver.toString(), xData, yData, null);
                    javax.swing.SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {


                            sw.repaintChart();
                        }
                    });

                    size++;
                }
                System.out.println(solver.toString());
            }
        } catch (Exception e ) {
            System.out.println(e);
        }
    }

    double[] copyData(double[] data, double[] globalData) {
        for (int j = 0; j < data.length; j++) {
            data[j] = globalData[j];
        }
        return data;
    }

    double[] removeZeroValues(double[] input) {
        int n = 0;
        // Get number of non-zero entries
        for (int i = 0; i < input.length; i++) {
            if (input[i] != 0) {
                n++;
            }
        }
        // Fill in new array with all the nonzero entries
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            double nextVal = 0;
            // Find next nonzero entry. If found, fill in the result array and change in input array to zero.
            for (int j = 0; j < input.length; j++) {
                if (input[j] != 0) {
                    nextVal = input[j];
                    input[j] = 0;
                    break;
                }
            }
            result[i] = nextVal;
        }
        return result;
    }

    static public void addSeries(XYChart chart, AbstractSolver solver, Solution solution) {
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

    public static void main(String[] args) {
        try {
            (new ChartMaker()).charts();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
