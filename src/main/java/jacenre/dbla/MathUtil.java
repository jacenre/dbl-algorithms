package jacenre.dbla;
/**
 * Util containing Mathematical functions, expressions, and constants.
 */
public class MathUtil {

    /**
     * Computes the Lambert value on the -1 branch with an error of at most 0.025%.
     * Cfr. https://reader.elsevier.com/reader/sd/pii/002216949390003R
     *
     * @param input value to compute Lambert value for.
     * @throws IllegalArgumentException if {@code input} not in domain [-1/e, 0).
     */
    public static double LambertMinusOne(double input) throws IllegalArgumentException {
        if (input < -1/Math.exp(1) || input >= 0) { // input not in [-1/e, 0)
			throw new IllegalArgumentException("Only defined on [-1/e, 0)");
		}

        final double sigma = -1-Math.log(-input); // for simplification of final expression

        // constants used in expression
        final double M1 = 0.3361;
        final double M2 = -0.0042;
        final double M3 = -0.0201;

        // final expression (expression A5, appendix)
        return -1-sigma - 2/M1*(1-1/(1+M1*Math.sqrt(sigma/2)/(1+M2*sigma*Math.exp(M3*Math.sqrt(sigma)))));
    }

    /**
     * Computes the Lambert value on the 0 branch with an error of at most 0.01%.
     * Cfr. https://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.90.6481&rep=rep1&type=pdf
     *
     * @param input value to compute Lambert value for.
     * @throws IllegalArgumentException if {@code input} not in domain [-1/e, infinity).
     */
    public static double LambertZero(double input) throws IllegalArgumentException {
        if (input < -1 / Math.exp(1)) { // input not in [-1/e, infinity)
			throw new IllegalArgumentException("Only defined on [-1/e, infinity)");
		}

        final double y = Math.sqrt(2*Math.exp(1)*input+2); // to simplify expression

        // constants used in expression
        final double A = 2.344;
        final double B = 0.8842;
        final double C = 0.9294;
        final double D = 0.5106;
        final double E = -1.213;

        // final expression (expression 40, section 4)
        return (2*Math.log(1+B*y) -Math.log(1+C*Math.log(1+D*y)) + E)
                / (1+ 1/(2 *Math.log(1+B*y)+2*A));
    }
}
