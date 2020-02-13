import org.junit.jupiter.api.DisplayName;

/**
 * Testing class using {@link TopLeftSolver}
 */
@DisplayName("Top-Left Solver")
public class TopLeftSolverTest extends AbstractPackingSolverTest {

    @Override
    AbstractSolver getSolver() {
        return new TopLeftSolver();
    }
}
