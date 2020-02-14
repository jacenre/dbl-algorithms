/**
 * Bin generator with Fixed height, non rotating
 */
public class FixedOptimalBinGenerator extends OptimalBinGenerator {
    
    @Override
    Parameters getParameters() {
        Parameters parameters = new Parameters();
        parameters.heightVariant = "free";
        parameters.rotationVariant = false;
        return parameters;
    }
}
