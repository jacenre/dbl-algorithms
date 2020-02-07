//TODO Make an abstract solver and then abstract this solver
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Test;

/**
 * Main class for the test cases
 *
 * Extend this abstract class and add a concrete binGenerator and Solver to test them.
 */
abstract class AbstractPackingSolverTest {

    // The bin generator being used for the test cases
    abstract AbstractBinGenerator getGenerator();

    /**
     * Basic testing structure,
     * The BinGenerator generates a input in the form of a Parameters object from the incomplete input.
     * The parameters then get passed on the solver and checked against the associated optimum from the bin object.
     * @param solver Solver to be used during the testing.
     * @param parameters The (incomplete) parameters to be passed on the BinGenerator.
     */
    private void testSolver(AbstractSolver solver, Parameters parameters) {
        Bin bin = getGenerator().generate(parameters);
        int optimal = solver.optimal(bin.parameters);

        // Test report
        System.out.println("Known optimal was :" + bin.optimal);
        System.out.println("Found optimal was :" + optimal);
        System.out.println("OPT rate of " + optimal / bin.optimal);
    }

    /**
     * Basic test to see if the binGenerator is working
     */
    @Test
    @Timeout(300000)
    void testBinGenerator() {
        Parameters parameters = new Parameters();
        parameters.heightVariant = (String) "free";
        parameters.rotationVariant = false;

        System.out.println(getGenerator().generate(parameters));
    }

}