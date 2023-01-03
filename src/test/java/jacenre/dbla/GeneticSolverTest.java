package jacenre.dbla;
import org.junit.jupiter.api.DisplayName;

import jacenre.dbla.AbstractSolver;
import jacenre.dbla.GeneticSolver;
import jacenre.dbla.TopLeftSolver;

/**
 * Testing class using {@link GeneticSolver}
 */
@DisplayName("Genetic Solver")
public class GeneticSolverTest extends AbstractPackingSolverTest {

    @Override
    AbstractSolver getSolver() {
        return new GeneticSolver(new TopLeftSolver(false), true);
    }

}
