import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Test;

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
        int optimal = solver.optimal(bin.parameters);
        double rate = (double) optimal /  (double) bin.optimal;
        // Test report
        System.out.println("Known optimal was :" + bin.optimal);
        System.out.println("Found optimal was :" + optimal);
        System.out.println("OPT rate of " + rate);
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

}