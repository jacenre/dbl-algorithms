import org.junit.jupiter.api.*;

/**
 * Implementation of the abstract test file using the OptimalBinGenerator
 */
public class OptimalBinGeneratorTest {

    /**
     * Implement the BinGenerator using the OptimalBinGenerator
     */
    private OptimalBinGenerator optimalBinGenerator = new OptimalBinGenerator();

    /**
     * Test to see if the rectangle generation works as expected.
     * Works by taking adding all the areas and checking if its equal to the optimum.
     */
    @Test
    void testRectangleGeneration() {
        Parameters parameters = new Parameters();
        parameters.heightVariant = "free";
        parameters.rotationVariant = false;

        Bin bin = optimalBinGenerator.generate(parameters);

        int area = 0;
        for (Rectangle rectangle :
                bin.parameters.rectangles) {
            area += (rectangle.width * rectangle.height);
        }
        Assertions.assertEquals(bin.optimal, area);
    }
}
