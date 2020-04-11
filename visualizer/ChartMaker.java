import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ChartMaker {
    public void areaAndTimeCharts() throws IOException {
        ArrayList<AbstractSolver> solvers = new ArrayList<>();
        solvers.add(new FirstFitSolver());
        solvers.add(new TopLeftSolver());
        solvers.add(new CompressionSolver());
        solvers.add(new ReverseFitSolver());
//        solvers.add(new SimpleTopLeftSolver());
//        solvers.add(new BottomUpSolver());

        //String path = "./test/input/Non-perfect fit/Bortfeldt, 2006";
        //String path = "./test/input/ChartSelection";
        //String path = "./test/input/selection";
        String path = "./test/input/Perfect fit/Hopper, 2000, set 1";

        File folder = new File(path);
        File[] files = folder.listFiles();
        assert files != null;

        // Filter files on .in extension.
        files = Arrays.stream(files)
                .filter(File::isFile)
                .filter(file -> file.getName().substring(file.getName().lastIndexOf(".") + 1).equals("in"))
                .toArray(File[]::new);

        CategoryChart areaChart = new CategoryChartBuilder().xAxisTitle("TestCases").yAxisTitle("Area").theme(Styler.ChartTheme.Matlab).width(1600).height(900).build();
        areaChart.getStyler().setYAxisMin(0.0);
        areaChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSE);
//        areaChart.getStyler().setXAxisTickMarkSpacingHint(1);

        CategoryChart timeChart = new CategoryChartBuilder().xAxisTitle("TestCases").yAxisTitle("Duration [ms]").theme(Styler.ChartTheme.Matlab).width(1600).height(900).build();
        timeChart.getStyler().setYAxisMin(0.0);
        timeChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSE);
//        timeChart.getStyler().setXAxisTickMarkSpacingHint(1);

        // Fill in the actual data
        for (AbstractSolver solver : solvers) {
            int i = 0;
            List<String> xData = new ArrayList<>();
            List<Number> areaData = new ArrayList<>();
            List<Number> timeData = new ArrayList<>();
//            double[] timeData = new double[files.length / 4];
            for (int i1 = 0; i1 < files.length; i1++) {
                File file = files[i1];
                if (i1 % 4 != 0) continue; // Only do the same variant of tests
                // yData last entry
                Parameters params = (new UserInput(new FileInputStream(file))).getUserInput();
                if (solver.canSolveParameters(params)) {
                    double startTime = System.nanoTime();
                    Solution solution = solver.getSolution(params.copy());
                    double endTime = System.nanoTime();
                    double duration = (endTime - startTime) / 1000000;
                    areaData.add(solution.getArea());
                    timeData.add(duration);
                }
                xData.add(file.getName().split(" ")[0]);
                i++;
            }
            timeChart.addSeries(solver.getName(), xData, timeData);
            areaChart.addSeries(solver.getName(), xData, areaData);
        }
//        new SwingWrapper<XYChart>(timeChart).displayChart();
//        new SwingWrapper<XYChart>(areaChart).displayChart();

        System.out.println(timeChart.getSeriesMap().get("FirstFitSolver"));
        /* Combine the csv files into a single file */
        String csvPath = "./csv/";

        // Create a map for each graph
        final Map<String, String> outMap = new HashMap<>();
        outMap.put("area", getCSVContent(areaChart));
        outMap.put("time", getCSVContent(timeChart));

        // Output the map to actual files
        for (String key : outMap.keySet()) {
            FileOutputStream outStream = new FileOutputStream(csvPath + key + ".csv");
            outStream.write(outMap.get(key).getBytes());
        }
    }

    static private String getCSVContent(CategoryChart chart) {
        String output = "test cases,";
        String[] solvers = chart.getSeriesMap().keySet().toArray(new String[0]);
        output += String.join(",", (List<String>) chart.getSeriesMap().get(solvers[0]).getXData()) + "\r\n";
        for (String solver : solvers) {
            output += solver + "," + chart.getSeriesMap().get(solver).getYData().stream().map(String::valueOf).collect(Collectors.joining(",")) + "\r\n";
        }
        return output;
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
                    bestScoreY[i] = (int) solution.getArea();
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
            (new ChartMaker()).areaAndTimeCharts();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
