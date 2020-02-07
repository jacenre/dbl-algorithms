/**
 * Implementation of the abstract test file using the OptimalBinGenerator
 */
public class OptimalPackingSolverTest extends AbstractPackingSolverTest{

    /**
     * Implement the BinGenerator using the OptimalBinGenerator
     * @return OptimalBinGenerator
     */
    @Override
    AbstractBinGenerator getGenerator() {
        return new OptimalBinGenerator();
    }
}
