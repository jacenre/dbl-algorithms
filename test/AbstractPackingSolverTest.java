import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Main class for the test cases
 */
@DisplayName("Concrete solver test cases")
abstract class AbstractPackingSolverTest {

    // The solver to be used for the test cases
    abstract AbstractSolver getSolver();

    /**
     * Test the solver against the gigantic library.
     */
//    @TestFactory
    @DisplayName("Solver Test Factory")
    @Tag("library")
    Stream<DynamicTest> dynamicSolverTests() throws IOException {
        List<DynamicTest> dynamicTests = new ArrayList<>();

        String path = "./test/input/selection";

        ArrayList<Double> average = new ArrayList<>();
        File folder = new File(path);
        File[] files = folder.listFiles();
        assert files != null;

        // Filter files on .in extension.
        files = Arrays.stream(files)
                .filter(File::isFile)
                .filter(file -> file.getName().substring(file.getName().lastIndexOf(".") + 1).equals("in"))
                .toArray(File[]::new);

        for (File file : files) {

            Parameters params = (new UserInput(new FileInputStream(file))).getUserInput();

            AbstractSolver solver = this.getSolver();
            if (!solver.canSolveParameters(params)) {
                continue;
            }

            DynamicTest dynamicTest = dynamicTest(file.getName(),
                    () -> assertTrue(Util.timedPacker(params, getSolver())));
            dynamicTests.add(dynamicTest);
        }
        return dynamicTests.stream();
    }

    /**
     * All momotor test cases
     */
    @TestFactory
    @DisplayName("Momotor Test Cases")
    @Tag("momotor")
    Stream<DynamicTest> momotorTests() throws IOException {
        List<DynamicTest> dynamicTests = new ArrayList<>();

        ArrayList<String> paths = new ArrayList<>();

        paths.add("./test/momotor/prototype-1");
        paths.add("./test/momotor/prototype-2");

        for (String path : paths) {
            // Get all files from the momotor folder
            File folder = new File(path);
            File[] files = folder.listFiles();
            assert files != null;
            files = Arrays.stream(files).filter(File::isFile).toArray(File[]::new);

            // Add a test for each input
            for (File file : files) {
                Parameters params = (new UserInput(new FileInputStream(file))).getUserInput();

                AbstractSolver solver = this.getSolver();
                if (!solver.canSolveParameters(params)) {
                    continue;
                }

                DynamicTest dynamicTest = dynamicTest(file.getName(),
                        () -> assertTrue(Util.timedPacker(params, getSolver())));

                dynamicTests.add(dynamicTest);
            }
        }

        return dynamicTests.stream();
    }

    @DisplayName("Benchmark")
    @Test
    public void benchmark() throws FileNotFoundException {
        // Use the hardest file as benchmark
        String path = "./test/momotor/benchmark_r1000-h1000-ry.in";
        File file = new File(path);
        Parameters params = (new UserInput(new FileInputStream(file))).getUserInput();

        long count = 0;

        AbstractSolver solver = getSolver();

        long duration = 0L;
        long startTime = System.nanoTime();
        long endTime;

        while (duration < 30000) {
            if (!solver.canSolveParameters(params)) {
                solver.getSolution(params);
                count++;
            }
            endTime = System.nanoTime();
            duration = (endTime - startTime) / 1000000;
        }
        System.out.println(solver.getClass().getSimpleName() + " reached a solve count of " + count);
        System.out.println("Where the params had a range of " + Util.largestRect(params) + " : " + Util.sumHeight(params));
    }

}