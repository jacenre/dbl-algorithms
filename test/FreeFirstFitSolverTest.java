import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Testing class using {@link FreeFirstFitSolver}
 */
@DisplayName("Free height First-Fit Solver")
public class FreeFirstFitSolverTest extends AbstractPackingSolverTest {


    @Override
    AbstractSolver getSolver() {
        return new FreeFirstFitSolver();
    }
}
