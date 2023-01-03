package jacenre.dbla;
import org.junit.jupiter.api.DisplayName;

import jacenre.dbla.AbstractSolver;
import jacenre.dbla.ReverseFitSolver;

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
