import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

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

    /**
     * Test to see if the rectangle generation works as expected.
     * Works by taking adding all the areas and checking if its equal to the optimum.
     */
    @Test
    void testRectangleGeneration() {
        Parameters parameters = new Parameters();
        parameters.heightVariant = (String) "free";
        parameters.rotationVariant = false;

        Bin bin = getGenerator().generate(parameters);

        int area = 0;
        for (Rectangle rectangle :
                bin.parameters.rectangles) {
            area += (rectangle.width * rectangle.height);
        }
        Assertions.assertEquals(bin.optimal, area);
    }
}
