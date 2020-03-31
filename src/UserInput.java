import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class UserInput {
    private Scanner sc;

    public UserInput(InputStream is) {
        this.sc = new Scanner(is);
    }

    public Parameters getUserInput() {
        Parameters params = new Parameters();
        List<Object> height = getHeight();

        if (height.get(0).equals("free")) {
            params.heightVariant = Util.HeightSupport.FREE;
        } else {
            params.heightVariant = Util.HeightSupport.FIXED;
        }

        params.height = (Integer) height.get(1);
        params.rotationVariant = getRotation();
        params.rectangles = getRectangles();
        return params;
    }

    private List<Object> getHeight() {
        // Getting the height variant
        String heightVariant = readInputParameter();
        int height = Integer.MAX_VALUE;
        if (heightVariant.contains("fixed")) {
            height = Integer.parseInt(heightVariant.split(" ")[1]);
            heightVariant = heightVariant.split(" ")[0];
        }
//        System.out.println();
//        System.out.println("Height variant: " + heightVariant + (height != Integer.MAX_VALUE ? " with height " + height : ""));

        return Arrays.asList(heightVariant, height);
    }

    private boolean getRotation() {
        // Getting the rotations variant
        String rotationsVariant = readInputParameter();
//        System.out.println("Rotations variant: " + rotationsVariant.startsWith("y"));
        return rotationsVariant.startsWith("y");
    }

    private ArrayList<Rectangle> getRectangles() {
        // Getting the rectangles
        int nRectangles = Integer.parseInt(readInputParameter());
        if (nRectangles <= 0) {
            throw new IllegalArgumentException("The number of rectangles must be positive.");
        }
//        System.out.println("Getting " + nRectangles + " rectangles");
        ArrayList<Rectangle> rectangles = new ArrayList<>();
        for (int i = 0; i < nRectangles; i++) {
            int[] rec = Arrays.stream(sc.nextLine().trim().split("\\s+")).mapToInt(Integer::parseInt).toArray();
            rectangles.add(new Rectangle(rec[0], rec[1]));
        }

//        for (Rectangle rec : rectangles) {
//            System.out.println(" " + rec.getSize());
//        }
        return rectangles;
    }

    private String readInputParameter() {
        return this.sc.nextLine().trim().split(":")[1].trim();
    }
}
