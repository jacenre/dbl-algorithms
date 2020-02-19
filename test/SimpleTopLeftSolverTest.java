import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

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
