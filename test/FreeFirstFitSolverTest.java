import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Testing class using {@link FreeFirstFitSolver}
 */
@DisplayName("Free height First-Fit Solver")
public class FreeFirstFitSolverTest extends AbstractPackingSolverTest {

    /**
     * List of Bin Generators for which the dynamic test should run.
     * @return All the dynamic generator you wish to run the algorithm on.
     */
    List<AbstractBinGenerator> getGenerators() {
        return Arrays.asList(new FixedOptimalBinGenerator(), new OptimalBinGenerator(),
                new RotatingOptimalBinGenerator(), new FixedRotatingOptimalBinGenerator());
    }

    @Override
    AbstractSolver getSolver() {
        return new FreeFirstFitSolver();
    }
}
