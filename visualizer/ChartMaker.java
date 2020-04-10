import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

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
        String path = "./test/input/selection";

        File folder = new File(path);
        File[] files = folder.listFiles();
        assert files != null;

        // Filter files on .in extension.
        files = Arrays.stream(files)
                .filter(File::isFile)
                .filter(file -> file.getName().substring(file.getName().lastIndexOf(".") + 1).equals("in"))
                .toArray(File[]::new);

        XYChart areaChart = new XYChartBuilder().xAxisTitle("TestCases").yAxisTitle("Area").theme(Styler.ChartTheme.Matlab).width(1600).height(900).build();
        areaChart.getStyler().setYAxisMin(0.0);
        areaChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSE);
        areaChart.getStyler().setXAxisTickMarkSpacingHint(1);

        XYChart timeChart = new XYChartBuilder().xAxisTitle("TestCases").yAxisTitle("Duration [ms]").theme(Styler.ChartTheme.Matlab).width(1600).height(900).build();
        timeChart.getStyler().setYAxisMin(0.0);
        timeChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSE);
        timeChart.getStyler().setXAxisTickMarkSpacingHint(1);

        // Fill in the actual data
        for (AbstractSolver solver : solvers) {
            int i = 0;
            double[] xData = new double[files.length];
            double[] areaData = new double[files.length];
            double[] timeData = new double[files.length];
            for (File file : files) {
                // yData last entry
                Parameters params = (new UserInput(new FileInputStream(file))).getUserInput();
                if (solver.canSolveParameters(params)) {
                    double startTime = System.nanoTime();
                    Solution solution = solver.getSolution(params.copy());
                    double endTime = System.nanoTime();
                    double duration = (endTime - startTime) / 1000000;
                    areaData[i] = (double) solution.getArea();
                    timeData[i] = duration;
                } else {
                    areaData[i] = 0;
                    timeData[i] = 0;
                }
                xData[i] = i;
                i++;
            }
            timeChart.addSeries(solver.getName(), xData, timeData);
            areaChart.addSeries(solver.getName(), xData, areaData);
        }
//        new SwingWrapper<XYChart>(timeChart).displayChart();
//        new SwingWrapper<XYChart>(areaChart).displayChart();
        CSVExporter.writeCSVRows(timeChart, "./csv/time");
        CSVExporter.writeCSVRows(areaChart, "./csv/area");

        /* Combine the csv files into a single file */
        String csvPath = "./csv/";
        File csvFolder = new File(csvPath);
        System.out.println(csvFolder.getAbsolutePath());
        File[] csvFiles = csvFolder.listFiles();
        // Filter files on .csv extension.
        assert csvFiles != null;
        csvFiles = Arrays.stream(csvFiles)
                .filter(File::isFile)
                .filter(file -> file.getName().substring(file.getName().lastIndexOf(".") + 1).equals("csv"))
                .toArray(File[]::new);

        // Create a map for each graph
        final Map<String, String> outMap = new HashMap<>();
        for (File file : csvFiles) {
            String name = file.getName();
            Scanner sc = new Scanner(new FileInputStream(file));

            String[] string = name.split("(?=\\p{Upper})");
            if (string.length <= 1) continue;
            String graph = string[0]; // For instance area or time
            String solverName = name.substring(graph.length(), name.length() - 4);
            if (!outMap.containsKey(graph)) {
                outMap.put(graph, "test cases," + sc.nextLine() + "\r\n");
            } else {
                sc.nextLine();
            }
            outMap.put(graph, outMap.get(graph) + solverName + "," + sc.nextLine() + "\r\n");
        }

        // Output the map to actual files
        for (String key: outMap.keySet()) {
            FileOutputStream outStream = new FileOutputStream(csvPath + key + ".csv");
            outStream.write(outMap.get(key).getBytes());
        }
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
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
