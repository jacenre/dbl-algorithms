import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

/**
 * Testing class using {@link TopLeftSolver}
 */
@DisplayName("Top-Left Solver")
public class TopLeftSolverTest extends AbstractPackingSolverTest {

    @Override
    AbstractSolver getSolver() {
        return new TopLeftSolver();
    }

    @Override
    List<AbstractBinGenerator> getGenerators() {
        return Arrays.asList(new FixedOptimalBinGenerator(), new FixedRotatingOptimalBinGenerator());
    }
}
