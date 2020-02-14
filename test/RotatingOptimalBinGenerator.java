/**
 * Implementation of the Optimal Bin Generator with rotating enabled.
 */
public class RotatingOptimalBinGenerator extends OptimalBinGenerator {

    @Override
    Parameters getParameters() {
        Parameters parameters = new Parameters();
        parameters.heightVariant = "free";
        parameters.rotationVariant = true;
        return parameters;
    }
}
