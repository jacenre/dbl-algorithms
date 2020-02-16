/**
 * Implementation of the Optimal Bin Generator with rotating enabled.
 */
public class RotatingOptimalBinGenerator extends OptimalBinGenerator {

    @Override
    Parameters getParameters() {
        Parameters parameters = new Parameters();
        parameters.heightVariant = Util.HeightSupport.FREE;
        parameters.rotationVariant = true;
        return parameters;
    }
}
