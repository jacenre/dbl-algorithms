import java.util.stream.StreamSupport;

/**
 * Abstract class for generator input test cases.
 */
abstract class AbstractBinGenerator {

    /**
     * Used to generate input.
     *
     * @return The bin to be packed
     */
    Bin generate(String heightVariant, boolean rotationVariant) {
        return new Bin(new Parameters(), 0);
    }

    /**
     * Used to generate input if the amount of rectangles is specified.
     *
     * @param n amount of rectangles to generate
     * @return The bin to be packed
     */
    Bin generate(int n, String heightVariant, boolean rotationVariant) {
        // default is ignore
        return generate(heightVariant, rotationVariant);
    }
}

/**
 * Class that stores the Parameters and additional satellite data of a test case.
 */
class Bin {

    Bin(Parameters parameters, int optimal) {
        this.parameters = parameters;
        this.optimal = optimal;
    }

    /**
     * The parameters used to generate the Bin
     */
    Parameters parameters;

    /**
     * If applicable, the optimal score of the bin
     */
    int optimal;
}