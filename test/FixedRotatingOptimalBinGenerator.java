/**
 * Implementation of the Optimal Bin Generator with rotating enabled.
 */
public class FixedRotatingOptimalBinGenerator extends OptimalBinGenerator {

    @Override
    Parameters getParameters() {
        Parameters parameters = new Parameters();
        parameters.heightVariant = "fixed";
        parameters.rotationVariant = true;
        return parameters;
    }
}
