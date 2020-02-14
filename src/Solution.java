/**
 * Intent: the solution object should contain all the information about the input and output
 * in such a way that only a solution object is needed to verify its correctness
 */
public class Solution {

    /**
     * The parameters that were used during solving.
     */
    public Parameters parameters;

    /**
     * The area the solution takes up
     */
    public int getArea(){
        try {
            return Math.multiplyExact(this.height, this.width);
        } catch (ArithmeticException e) {
            return Integer.MAX_VALUE;
        }
    };

    /**
     * Height of the total bin area
     */
    public int height;

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Width of the total bin area
     */
    public int width;

    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Create a solution object without knowing the solution.
     * @param parameters The parameters used for solving.
     */
    public Solution(Parameters parameters) {
        this.parameters = parameters;
    }

    /**
     * Create a Solution object when you know the width and height of the solution.
     * @param width The width of the solution.
     * @param height The height of the solution.
     * @param parameters The parameters used for solving.
     */
    public Solution(int width, int height, Parameters parameters) {
        this.width = width;
        this.height = height;
        this.parameters = parameters;
    }
}
