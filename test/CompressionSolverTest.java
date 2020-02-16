import org.junit.jupiter.api.DisplayName;

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

    @Override
    List<AbstractBinGenerator> getGenerators() {
        return Arrays.asList(new FixedOptimalBinGenerator(), new FixedRotatingOptimalBinGenerator());
    }
}
