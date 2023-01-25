package jacenre.dbla;
import org.junit.jupiter.api.DisplayName;

/**
 * Testing class using {@link SimpleTopLeftSolver}
 */
@DisplayName("Simple Top-Left Solver")
public class SimpleTopLeftSolverTest extends AbstractPackingSolverTest {

    @Override
    AbstractSolver getSolver() {
        return new SimpleTopLeftSolver();
    }

}
