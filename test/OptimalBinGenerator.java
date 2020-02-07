import java.util.ArrayList;
import java.util.Random;

/**
 * Class that generates optimal test cases to be used as input for tests.
 */
public class OptimalBinGenerator extends AbstractBinGenerator {

    private Random generator = new Random();
    private Long SEED = 10L;

    OptimalBinGenerator() {
        generator.setSeed(SEED);
    }

    /**
     * Used to generate input.
     * Works by creating a rectangle with a known size and then cutting it up in squares.
     *
     * @return The bin to be packed
     */
    @Override
    Bin generate(Parameters parameters) {
        //TODO find a way to parametrize the bound

        // random grid size
        int randomHeight = generator.nextInt((int)Math.sqrt(Integer.MAX_VALUE));
        int randomWidth =  generator.nextInt((int)Math.sqrt(Integer.MAX_VALUE));
        System.out.println("Generating grid with height = " + randomHeight + ", width = " + randomWidth);
        // optimal is the square grid size
        int optimal = randomHeight * randomWidth;

        // Fixed height is the generated height.
        parameters.height = randomHeight;

        ArrayList<Rectangle> rectangles = new ArrayList<>();
        recursiveRectangle(rectangles, randomWidth, randomHeight, true);

        parameters.rectangles = rectangles;
        return new Bin(parameters, optimal);
    }

    // Used for the recursive rectangle generation
    private static final int MIN_RECT_SIZE = 5;

    /**
     * Recursively cut up the rectangle and add them to the array.
     * @param rectangles Array that the results should be added to.
     * @param width The width of the rectangle in the recursion.
     * @param height The height of the rectangle in the recursion.
     * @param vert True if we are cutting the rectangle vertically, false if horizontal
     */
    private void recursiveRectangle(ArrayList<Rectangle> rectangles, int width, int height, boolean vert) {
        double cut = generator.nextDouble();

        // Return if the recursion would give rise to rectangle smaller than the minimum, else cut and recurse.
        if (vert) {
            if (width * cut < MIN_RECT_SIZE || width * (1 - cut) < MIN_RECT_SIZE) {
                rectangles.add(new Rectangle(width, height));
            } else {
                // Used to make sure that the total width doesnt change due to rounding errors.
                int newWidth = (int) (width * cut);
                recursiveRectangle(rectangles, newWidth, height, false);
                recursiveRectangle(rectangles, width - newWidth, height, false);
            }
        } else if (height * cut < MIN_RECT_SIZE || height * (1 - cut) < MIN_RECT_SIZE) {
            rectangles.add(new Rectangle(width, height));
        } else {
            int newHeight = (int) (height * cut);
            recursiveRectangle(rectangles, width, newHeight, true);
            recursiveRectangle(rectangles, width, height - newHeight, true);
        }
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
