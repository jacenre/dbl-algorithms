package jacenre.dbla;
import org.junit.jupiter.api.DisplayName;

import jacenre.dbla.AbstractSolver;
import jacenre.dbla.SimpleTopLeftSolver;

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
