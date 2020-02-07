/**
 * Testing class using {@link FirstFitSolver}
 */
public class FirstFitSolverTest extends AbstractPackingSolverTest {

    @Override
    AbstractSolver getSolver() {
        return new FirstFitSolver();
    }
}
