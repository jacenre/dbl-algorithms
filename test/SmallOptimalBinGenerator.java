public class SmallOptimalBinGenerator extends OptimalBinGenerator {
    @Override
    int getHeight() {
        return 2000;
    }

    @Override
    int getWidth() {
        return 2000;
    }

    @Override
    public int getMinRectSize() {
        return 10;
    }
}
