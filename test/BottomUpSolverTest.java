import org.junit.jupiter.api.DisplayName;
import java.util.Arrays;
import java.util.List;

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
