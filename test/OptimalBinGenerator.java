import java.util.ArrayList;
import java.util.Random;

/**
 * Class that generates optimal test cases to be used as input for tests.
 */
public class OptimalBinGenerator extends AbstractBinGenerator {

    /**
     * Used to generate input.
     * Works by creating a rectangle with a know size and then cutting it up in squares.
     *
     * @return The bin to be packed
     */
    @Override
    Bin generate(Parameters parameters) {
        //TODO find a way to parametrize the bound and add a seed

        // random grid size
        int randomHeight = new Random().nextInt(1000);
        int randomWidth = new Random().nextInt(1000);

        // optimal is the square grid size
        int optimal = randomHeight * randomWidth;

        // Fixed height is the optimal height.
        parameters.height = randomHeight;

        // looks like a circular dependency but it isn't.
        parameters.rectangles = generateRectangles(parameters);

        return new Bin(parameters, optimal);
    }

    /**
     * Method used to generate rectangles based of optimal parameters.
     * @param parameters The parameters used in the bin
     * @return rectangles
     */
    private ArrayList<Rectangle> generateRectangles(Parameters parameters) {
        ArrayList<Rectangle> rectangles = new ArrayList<>();


        return rectangles;
    }

    /**
     * Used to generate input if the amount of rectangles is specified.
     *
     * @param n amount of rectangles to generate
     * @return The bin to be packed
     */
    @Override
    Bin generate(int n, Parameters parameters) {
        return null;
    }
}
