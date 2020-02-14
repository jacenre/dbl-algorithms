/**
 * Abstract class for generator input test cases.
 */
abstract class AbstractBinGenerator {

    /**
     * Used to generate input.
     * @param parameter The parameter used to describe the input.
     * @return The bin to be packed.
     */
    Bin generate(Parameters parameter) {
        return new Bin(new Parameters(), 0);
    }

    /**
     * Used to generate input if the amount of rectangles is specified.
     *
     * @param n amount of rectangles to generate
     * @param parameter The parameter used to describe the input.
     * @return The bin to be packed
     */
    Bin generate(int n, Parameters parameter) {
        // default is to ignore n
        return generate(parameter);
    }
}

/**
 * Class that stores the Parameters and additional satellite data of a test case.
 */
class Bin {

    Bin(Parameters parameters, Integer optimal) {
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
    Integer optimal;
}