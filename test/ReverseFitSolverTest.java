import org.junit.jupiter.api.DisplayName;
import java.util.Arrays;
import java.util.List;

/**
 * Testing class using {@link ReverseFitSolver}
 */
@DisplayName("Reverse-Fit Solver")
public class ReverseFitSolverTest extends AbstractPackingSolverTest {

    @Override
    AbstractSolver getSolver() {
        return new ReverseFitSolver();
    }

    @Override
    List<AbstractBinGenerator> getGenerators() {
        return Arrays.asList(new FixedOptimalBinGenerator(), new FixedRotatingOptimalBinGenerator());
    }
}
