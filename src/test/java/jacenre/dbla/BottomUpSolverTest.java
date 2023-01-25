package jacenre.dbla;
import org.junit.jupiter.api.DisplayName;

/**
 * Testing class using {@link BottomUpSolverTest}
 */
@DisplayName("Bottom-up Solver")
public class BottomUpSolverTest extends AbstractPackingSolverTest {

    @Override
    AbstractSolver getSolver() {
        return new BottomUpSolver();
    }

}
