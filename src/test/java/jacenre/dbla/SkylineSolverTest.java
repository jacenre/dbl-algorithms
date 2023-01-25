package jacenre.dbla;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

/**
 * Testing class using {@link SkylineSolverTest}
 */
@DisplayName("Skyline Solver")
@Disabled("Really slow!")
public class SkylineSolverTest extends AbstractPackingSolverTest {

    @Override
    AbstractSolver getSolver() {
        return new SkylineSolver();
    }

}
