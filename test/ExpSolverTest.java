import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

/**
 * Testing class using {@link TopLeftSolver}
 */
@DisplayName("Top-Left Solver")
public class ExpSolverTest extends AbstractPackingSolverTest {

    @Override
    AbstractSolver getSolver() {
        return new ExpSolver();
    }

    @Override
    List<AbstractBinGenerator> getGenerators() {
        return Arrays.asList(new FixedOptimalBinGenerator(), new FixedRotatingOptimalBinGenerator());
    }
}
