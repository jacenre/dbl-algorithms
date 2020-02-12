import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertTimeout;

/**
 * Main class for the test cases
 *
 * Extend this abstract class and add a concrete binGenerator and Solver to test them.
 */
abstract class AbstractPackingSolverTest {
    // The solver to be used for the test cases
    abstract AbstractSolver getSolver();

    /**
     * Basic testing structure,
     * The BinGenerator generates a input in the form of a Parameters object from the incomplete input.
     * The parameters then get passed on the solver and checked against the associated optimum from the bin object.
     * @param solver Solver to be used during the testing.
     * @param bin The bin containing the grid and the expected result.
     */
    @Timeout(30000)
    private void testSolver(AbstractSolver solver, Bin bin) {
        // Log how long it took to solve
        long startTime = System.nanoTime();

        int[] sol = solver.optimal(bin.parameters);
        int optimal = sol[0] * sol[1];

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        double rate = (double) optimal /  (double) bin.optimal;

        // Test report
        System.out.println("Known optimal was :" + bin.optimal);
        System.out.println("Found optimal was :" + optimal);
        System.out.println("OPT rate of " + rate);
        System.out.println("Solve took " + duration/1000000 + "ms");

        if (bin.parameters.heightVariant.equals("fixed")) {
            Assertions.assertEquals(bin.parameters.height,sol[0]);
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

    /**
     * Automatic testing
     */
    @Test
    void automatedTestCase() {
        OptimalBinGenerator binGenerator = new OptimalBinGenerator();

        for (int i = 0; i < 10; i++) {
            AbstractSolver solver = getSolver();

            // Parameters to be used for the test.
            Parameters parameters = new Parameters();
            parameters.heightVariant = "fixed";
            parameters.rotationVariant = false;

            assertTimeout(ofSeconds(30), () -> testSolver(solver, binGenerator.generate(parameters)),
                    "Solve attempt took longer than 30 seconds.");

        }
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