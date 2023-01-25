package jacenre.dbla;
import org.junit.jupiter.api.DisplayName;

/**
 * Testing class using {@link TopLeftSolver}
 */
@DisplayName("Compression Solver")
public class CompressionSolverTest extends AbstractPackingSolverTest {

    @Override
    AbstractSolver getSolver() {
        return new CompressionSolver();
    }

}
