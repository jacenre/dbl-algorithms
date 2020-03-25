import org.junit.jupiter.api.DisplayName;

/**
 * Testing class using {@link SkylineSolverTest}
 */
@DisplayName("Skyline Solver")
public class SkylineSolverTest extends AbstractPackingSolverTest {

    @Override
    AbstractSolver getSolver() {
        return new SkylineSolver();
    }

}
