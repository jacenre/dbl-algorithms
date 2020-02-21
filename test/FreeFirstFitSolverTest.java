import org.junit.jupiter.api.DisplayName;

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
