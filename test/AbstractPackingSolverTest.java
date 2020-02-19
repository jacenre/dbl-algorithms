import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Main class for the test cases
 * <p>
 * Extend this abstract class and add a concrete binGenerator and Solver to test them.
 */
@DisplayName("Concrete solver test cases")
abstract class AbstractPackingSolverTest {
    // The solver to be used for the test cases
    abstract AbstractSolver getSolver();

//    /**
//     * Basic testing structure,
//     * The BinGenerator generates a input in the form of a Parameters object from the incomplete input.
//     * The parameters then get passed on the solver and checked against the associated optimum from the bin object.
//     *
//     * @param solver Solver to be used during the testing.
//     * @param bin    The bin containing the grid and the expected result.
//     */
//    private void testSolver(AbstractSolver solver, Bin bin) {
//        // Log how long it took to solve
//        long startTime = System.nanoTime();
//
//        Solution sol = solver.optimal(bin.parameters);
//        int optimal = sol.getArea();
//
//        long endTime = System.nanoTime();
//        long duration = (endTime - startTime);
//
//        double rate = (double) optimal / (double) bin.optimal;
//
//        // Test report
//        System.out.println("Known optimal was :" + bin.optimal);
//        System.out.println("Found optimal was :" + optimal);
//        System.out.println("OPT rate of " + rate);
//        System.out.println("Solve took " + duration / 1000000 + "ms");
//
//        if (bin.parameters.heightVariant.equals("fixed")) {
//            Assertions.assertEquals(bin.parameters.height, sol.height);
//        }
//        Assertions.assertTrue(rate >= 1);
//    }

//    /**
//     * Example structure of a test case using the OptimalBinGenerator
//     */
//    @Test
//    void exampleTestCase() {
//        AbstractSolver solver = getSolver();
//        Assumptions.assumeTrue(solver.heightSupport.contains(HeightSupport.FIXED));
//        OptimalBinGenerator binGenerator = new OptimalBinGenerator();
//
//        Bin bin = binGenerator.generate();
//
//        testSolver(solver, bin);
//    }

    /**
     * Amount of test to run in the TestFactory
     *
     * @return default 10 test cases.
     */
    int getTestCount() {
        return 10;
    }

    /**
     * List of BinGenerators.Bin Generators for which the dynamic test should run.
     *
     * @return All the dynamic generator you wish to run the algorithm on.
     */
    List<AbstractBinGenerator> getGenerators() {
        return Arrays.asList(new FixedOptimalBinGenerator(), new OptimalBinGenerator(),
                new RotatingOptimalBinGenerator(), new FixedRotatingOptimalBinGenerator());
    }

    public Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    /**
     * Tests all the generators in binGenerators against the concrete solver
     */
    @TestFactory
    @DisplayName("Solver Test Factory")
    Stream<DynamicTest> dynamicSolverTests() throws FileNotFoundException {
        List<DynamicTest> dynamicTests = new ArrayList<>();

        String path = "/home/samuel/IdeaProjects/dbl-algorithms/test/input/Non-perfect fit/Martello, 1998";

        File folder = new File(path);
        File[] files = folder.listFiles();
        assert files != null;
        files = Arrays.stream(files).filter(File::isFile).toArray(File[]::new);

        for (File file : files) {

            Parameters params = (new UserInput(new FileInputStream(file))).getUserInput();
            Bin bin = new Bin(params, null);
            AbstractSolver solver = this.getSolver();
            if (!solver.getHeightSupport().contains(params.heightVariant)) {
                continue;
            }
            DynamicTest dynamicTest = dynamicTest(file.getName(), () -> assertTrue(isValidSolution(bin)));

            dynamicTests.add(dynamicTest);
        }

        return dynamicTests.stream();
    }

    /**
     * Check if the solution found by the solver is valid for the bin
     *
     * @param bin The bin containing the precomputed optimal.
     * @return True if the solution is valid, else false.
     */
    boolean isValidSolution(Bin bin) {
        // Log how long it took to solve
        long startTime = System.nanoTime();

        AbstractSolver solver = getSolver();

        Solution sol = solver.optimal(bin.parameters);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        Double rate;
        rate = sol.getRate();

        // Test report
        System.out.println(sol);
        System.out.println("Solve took " + duration / 1000000 + "ms");

        if (hasOverlapping(sol.parameters.rectangles)) {
            System.err.println("There are overlapping rectangles");
            return false;
        }

        for (Rectangle rectangle :
                sol.parameters.rectangles) {
            if (rectangle.x < 0 || rectangle.y < 0) {
                System.err.println("Negative coordinates found");
                return false;
            }
        }

        // If solve took longer than 30 seconds
        if ((duration / 1000000) > 30000) {
            System.err.println("Time limit reached");
            return false;
        }

        if (bin.parameters.heightVariant == Util.HeightSupport.FIXED) {
            if (sol.getHeight() > bin.parameters.height) {
                System.err.println("The height limit is not maintained");
                return false;
            }
        }

        if (rate < 1) {
            System.err.println("Impossible result.");
            return false;
        }

        return true;
    }

    /**
     * Test to see if any of the rectangles in the list overlap.
     *
     * @return True if overlapping rectangles in list else false.
     * TODO Improve runtime
     */
    boolean hasOverlapping(ArrayList<Rectangle> rectangles) {
        for (Rectangle rectangle1 :
                rectangles) {
            for (Rectangle rectangle2 :
                    rectangles) {
                if (!rectangle1.equals(rectangle2)) {
                    if (rectangle1.intersects(rectangle2)) {
                        System.out.println(rectangle1.getId());
                        System.out.println(rectangle2.getId());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * All momotor test cases
     */
    @TestFactory
    @DisplayName("Momotor Test Cases")
    Stream<DynamicTest> momotorTests() throws IOException {
        List<DynamicTest> dynamicTests = new ArrayList<>();

        ArrayList<String> paths = new ArrayList<String>();

//        paths.add("./test/momotor/prototype-1");
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
                Bin bin = new Bin(params, null);
                AbstractSolver solver = this.getSolver();
                if (!solver.getHeightSupport().contains(params.heightVariant)) {
                    continue;
                }
                DynamicTest dynamicTest = dynamicTest(file.getName(), () -> assertTrue(isValidSolution(bin)));

                dynamicTests.add(dynamicTest);
            }
        }

        return dynamicTests.stream();

    }

}