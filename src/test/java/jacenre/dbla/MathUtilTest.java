package jacenre.dbla;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.jafama.FastMath;

class MathUtilTest {

	@Test
	void lambertMinusOne() {
		double result = MathUtil.LambertMinusOne(-1 / FastMath.exp(1));
		System.out.println(result);
		assertTrue(Math.abs(result - (-1)) < 0.025);
	}

	@Test
	void lambertZero() {
		double result = MathUtil.LambertZero(0);
		assertTrue(Math.abs(result - 0) < 0.01);

		result = MathUtil.LambertZero(-1 / FastMath.exp(1));
		System.out.println(result);
		assertTrue(Math.abs(result - (-1)) < 0.025);
	}
}