import java.awt.*;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private static Scanner sc;

    /**
     * @throws IllegalArgumentException  if number of rectangles is not positive
     */
    public static void main() throws IllegalArgumentException {
        sc = new Scanner(System.in);

        // Getting the height variant
        String heightVariant = inputParameter();
        Integer height = null;
        if (heightVariant.contains("fixed")) {
            height = Integer.parseInt(heightVariant.split(" ")[1]);
            heightVariant = heightVariant.split(" ")[0];
        }
        System.out.println();
        System.out.println("Height variant: " + heightVariant + (height != null ? " with height " + height : ""));

        // Getting the rotations variant
        String rotationsVariant = inputParameter();
        System.out.println("Rotations variant: " + rotationsVariant);

        // Getting the rectangles
        int nRectangles = Integer.parseInt(inputParameter());
        if (nRectangles <= 0) {
            throw new IllegalArgumentException("The number of rectangles must be positive.");
        }
        System.out.println("Getting " + nRectangles + " rectangles");
        Rectangle[] rectangles = new Rectangle[nRectangles];
        for (int i = 0; i < nRectangles; i++) {
            int[] rec = Arrays.stream(sc.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
            rectangles[i] = new Rectangle(rec[0], rec[1]);
        }

        for (Rectangle rec: rectangles) {
            System.out.println(" " + rec.getSize());
        }
    }

    private static String inputParameter () {
        return sc.nextLine().split(":")[1].trim();
    }
}
