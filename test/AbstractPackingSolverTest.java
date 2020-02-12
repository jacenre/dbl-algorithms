import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertTimeout;
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

    /**
     * Basic testing structure,
     * The BinGenerator generates a input in the form of a Parameters object from the incomplete input.
     * The parameters then get passed on the solver and checked against the associated optimum from the bin object.
     *
     * @param solver Solver to be used during the testing.
     * @param bin    The bin containing the grid and the expected result.
     */
    private void testSolver(AbstractSolver solver, Bin bin) {
        // Log how long it took to solve
        long startTime = System.nanoTime();

        Solution sol = solver.optimal(bin.parameters);
        int optimal = sol.getArea();

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        double rate = (double) optimal / (double) bin.optimal;

        // Test report
        System.out.println("Known optimal was :" + bin.optimal);
        System.out.println("Found optimal was :" + optimal);
        System.out.println("OPT rate of " + rate);
        System.out.println("Solve took " + duration / 1000000 + "ms");

        if (bin.parameters.heightVariant.equals("fixed")) {
            Assertions.assertEquals(bin.parameters.height, sol.height);
        }
        Assertions.assertTrue(rate >= 1);
    }

    /**
     * Example structure of a test case using the OptimalBinGenerator
     */
    @Test
    void exampleTestCase() {
        AbstractSolver solver = getSolver();
        OptimalBinGenerator binGenerator = new OptimalBinGenerator();

        // Parameters to be used for the test.
        Parameters parameters = new Parameters();
        parameters.heightVariant = "fixed";
        parameters.rotationVariant = false;

        testSolver(solver, binGenerator.generate(parameters));
    }

    private int TEST_COUNT = 10;
    private List<AbstractBinGenerator> binGenerators = Arrays.asList(new OptimalBinGenerator(),
            new LargeOptimalBinGenerator());

    /**
     * Tests all the generators in binGenerators against the concrete solver
     */
    @TestFactory
    @DisplayName("Dynamic Test")
    Stream<DynamicTest> dynamicSolverTests() {
        List<DynamicTest> dynamicTests = new ArrayList<>();

        for (AbstractBinGenerator binGenerator :
                binGenerators) {

            for (int i = 0; i < TEST_COUNT; i++) {

                // TODO Make generators for each parameter combo.
                Parameters parameters = new Parameters();
                parameters.heightVariant = "fixed";
                parameters.rotationVariant = false;
                Bin bin = binGenerator.generate(parameters);
                DynamicTest dynamicTest = dynamicTest(binGenerator.getClass().getSimpleName() + " #" + i, ()
                        -> {
                    assertTrue(isValidSolution(bin));
                });

                dynamicTests.add(dynamicTest);
            }
        }
        return dynamicTests.stream();
    }

    /**
     * Check if the solution found by the solver is valid for the bin
     * @param bin The bin containing the precomputed optimal.
     * @return True if the solution is valid, else false.
     */
    boolean isValidSolution(Bin bin) {
        // Log how long it took to solve
        long startTime = System.nanoTime();

        AbstractSolver solver = getSolver();

        Solution sol = solver.optimal(bin.parameters);
        int optimal = sol.getArea();

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        double rate = (double) optimal / (double) bin.optimal;

        // Test report
        System.out.println("Known optimal was :" + bin.optimal);
        System.out.println("Found optimal was :" + optimal);
        System.out.println("OPT rate of " + rate);
        System.out.println("Solve took " + duration / 1000000 + "ms");

        // If solve took longer than 30 seconds
        if ((duration / 1000000) > 30000) {
            return false;
        }

        if (bin.parameters.heightVariant.equals("fixed")) {
            if (bin.parameters.height != sol.height) {
                return false;
            }
        }

        return rate >= 1;
    }

    /**
     * Momotor cases testing
     */
    @Test
    void momotorTestCase1() {
        OptimalBinGenerator binGenerator = new OptimalBinGenerator();

        AbstractSolver solver = getSolver();

        Parameters parameters = new Parameters();
        parameters.heightVariant = "fixed";
        parameters.rotationVariant = false;

        assertTimeout(ofSeconds(30), () -> testSolver(solver, binGenerator.generate(parameters)),
                "Solve attempt took longer than 30 seconds.");
    }
}