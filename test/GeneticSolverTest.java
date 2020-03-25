import org.junit.jupiter.api.DisplayName;

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
