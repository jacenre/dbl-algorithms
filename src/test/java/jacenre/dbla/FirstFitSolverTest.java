package jacenre.dbla;
import org.junit.jupiter.api.DisplayName;

/**
 * Testing class using {@link FirstFitSolver}
 */
@DisplayName("First-Fit Solver")
public class FirstFitSolverTest extends AbstractPackingSolverTest {

    @Override
    AbstractSolver getSolver() {
        return new FirstFitSolver();
    }
}
