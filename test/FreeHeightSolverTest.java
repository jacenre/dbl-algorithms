import org.junit.jupiter.api.DisplayName;

/**
 * Testing class using {@link FreeHeightSolver}
 */
@DisplayName("Free height First-Fit Solver")
public class FreeHeightSolverTest extends AbstractPackingSolverTest {


    @Override
    AbstractSolver getSolver() {
        return new FreeHeightSolver();
    }
}
