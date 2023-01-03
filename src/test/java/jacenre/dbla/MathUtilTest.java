package jacenre.dbla;
import org.junit.jupiter.api.Test;

import jacenre.dbla.MathUtil;

import static org.junit.jupiter.api.Assertions.*;

class MathUtilTest {

    @Test
    void lambertMinusOne() {
        double result = MathUtil.LambertMinusOne(-1/Math.exp(1));
        System.out.println(result);
        assertTrue(Math.abs(result - (-1)) < 0.025);
    }

    @Test
    void lambertZero() {
        double result = MathUtil.LambertZero(0);
        assertTrue(Math.abs(result - 0) < 0.01);

        result = MathUtil.LambertZero(-1/Math.exp(1));
        System.out.println(result);
        assertTrue(Math.abs(result - (-1)) < 0.025);
    }
}