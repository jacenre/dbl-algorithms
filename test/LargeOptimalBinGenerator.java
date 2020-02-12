import java.util.Map;

/**
 * Implementation of the Optimal Bin Generator where te bin has a minimum area of 0.25*MAX_INTEGER
 */
public class LargeOptimalBinGenerator extends OptimalBinGenerator {

    @Override
    int getHeight() {
        return (int) (Math.sqrt(0.10)* Math.sqrt(Integer.MAX_VALUE) +
                generator.nextInt((int)(Math.sqrt(0.10)*Math.sqrt(Integer.MAX_VALUE))));
    }

    @Override
    int getWidth() {
        return (int) (Math.sqrt(0.10)* Math.sqrt(Integer.MAX_VALUE) +
                generator.nextInt((int)(Math.sqrt(0.10)*Math.sqrt(Integer.MAX_VALUE))));
    }

    @Override
    public int getMinRectSize() {
        return generator.nextInt(10);
    }
}
