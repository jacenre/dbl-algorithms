package jacenre.dbla;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * Main class for the test cases
 */
@DisplayName("Concrete solver test cases")
@Execution(ExecutionMode.CONCURRENT)
abstract class AbstractPackingSolverTest {

    // The solver to be used for the test cases
    abstract AbstractSolver getSolver();

    /**
     * Test the solver against the gigantic library.
     */
    @TestFactory
    @DisplayName("Solver Test Factory")
    @Tag("library")
    Stream<DynamicTest> dynamicSolverTests() throws IOException {
        List<DynamicTest> dynamicTests = new ArrayList<>();

//        String path = "./test/input/Non-perfect fit/Bortfeldt, 2006";
//        String path = "./test/input/Perfect fit/Pinto, 2005";
        String path = Paths.get("src","test","resources", "input/Perfect fit/Ramesh Babu, 1999").toString();

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

        paths.add(Paths.get("src","test","resources", "momotor/custom").toString());
        paths.add(Paths.get("src","test","resources", "momotor/prototype-1").toString());
        paths.add(Paths.get("src","test","resources", "momotor/prototype-2").toString());

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
    @Disabled("Benchmark disabled.")
    public void benchmark() throws FileNotFoundException {
        // Use the hardest file as benchmark
    	
        File file = Paths.get("src","test","resources", "momotor/benchmark_r1000-h1000-ry.in").toFile();
        Parameters params = (new UserInput(new FileInputStream(file))).getUserInput();

        long count = 0;

        AbstractSolver solver = getSolver();

        long duration = 0L;
        long startTime = System.nanoTime();
        long endTime;

        while (duration < 30000) {
            if (solver.canSolveParameters(params)) {
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