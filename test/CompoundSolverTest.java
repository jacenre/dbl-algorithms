import org.junit.jupiter.api.DisplayName;

/**
 * Testing class using {@link CompoundSolver}
 */
@DisplayName("Simple Compound Solver")
public class CompoundSolverTest extends AbstractPackingSolverTest {

    @Override
    AbstractSolver getSolver() {
        CompoundSolver compoundSolver = new CompoundSolver();
        compoundSolver.addSolver(new FirstFitSolver());
        compoundSolver.addSolver(new TopLeftSolver());
        compoundSolver.addSolver(new CompressionSolver());
        compoundSolver.addSolver(new FreeHeightSolver());
        compoundSolver.addSolver(new ReverseFitSolver());
        compoundSolver.addSolver(new SimpleTopLeftSolver());
        return compoundSolver;
    }
}
