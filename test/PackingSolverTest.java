import static org.junit.jupiter.api.Assertions.*;

//TODO Make an abstract solver and then abstract this solver

/**
 * Main class for the test cases
 *
 * Extend this abstract class and add a concrete binGenerator and Solver to test them.
 */
abstract class PackingSolverTest {

    // The bin generator being used for the test cases
    private AbstractBinGenerator abstractBinGenerator;

    private void testSolver(AbstractSolver solver, Parameters parameters) {
        Bin bin = abstractBinGenerator.generate(parameters);
    }

}