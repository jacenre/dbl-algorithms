import org.junit.jupiter.api.DisplayName;

/**
 * Testing class using {@link ReverseFitSolver}
 */
@DisplayName("Reverse-Fit Solver")
public class ReverseFitSolverTest extends AbstractPackingSolverTest {

    @Override
    AbstractSolver getSolver() {
        return new ReverseFitSolver();
    }

}
