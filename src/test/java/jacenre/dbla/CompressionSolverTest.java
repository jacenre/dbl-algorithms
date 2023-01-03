package jacenre.dbla;
import org.junit.jupiter.api.DisplayName;

import jacenre.dbla.AbstractSolver;
import jacenre.dbla.CompressionSolver;
import jacenre.dbla.TopLeftSolver;

import java.util.Arrays;
import java.util.List;

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
