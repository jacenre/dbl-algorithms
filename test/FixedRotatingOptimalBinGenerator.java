/**
 * Implementation of the Optimal Bin Generator with rotating enabled.
 */
public class FixedRotatingOptimalBinGenerator extends OptimalBinGenerator {

    @Override
    Parameters getParameters() {
        Parameters parameters = new Parameters();
        parameters.heightVariant = HeightSupport.FIXED;
        parameters.rotationVariant = true;
        return parameters;
    }
}
