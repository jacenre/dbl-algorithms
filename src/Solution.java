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
        return this.height * this.width;
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

    public Solution(Parameters parameters) {
        this.parameters = parameters;
    }
}
