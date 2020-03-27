import java.awt.*;
import java.util.ArrayList;

public class ArrayListSkyline extends AbstractSkyline {

    ArrayList<Segment> skyline;
    int globalHeight;
    int globalWidth;
    int maximumSpread;

    public ArrayListSkyline(int height, int width, int maximumSpread) {
        this.globalHeight = height;
        this.globalWidth = width;
        this.maximumSpread = maximumSpread;

        skyline = new ArrayList<>();
        skyline.add(new Segment(new SegPoint(true, 0, 0), new SegPoint(false, 0, height)));
    }

    @Override
    public void removeRectangle(Rectangle rectangle) {

    }

    /**
     * Computes all the candidate positions (where a new rectangle could be placed), given a skyline and width
     *
     * @returns the list of points on which new rectangles can be placed
     */
    @Override
    public ArrayList<SegPoint> getCandidatePoints() {
        ArrayList<SegPoint> points = new ArrayList<>();
        if (skyline.get(0).start.x < globalWidth) {
            points.add(skyline.get(0).start);
        }

        // Check all the middle ones
        for (int i = 0; i < skyline.size(); i++) {
            if (i != 0 && skyline.get(i).start.x < skyline.get(i-1).end.x) {
                points.add(skyline.get(i).start);
            }
            if (i != skyline.size() - 1 && skyline.get(i).end.x < skyline.get(i+1).start.x) {
                points.add(skyline.get(i).end);
            }
        }

        if (skyline.get(skyline.size() - 1).end.x < globalWidth) {
            points.add(skyline.get(skyline.size() - 1).end);
        }
        return points;
    }


    /**
     * Returns the most left segment in the skyline. Usefull for checking if a newly placed rectangles breaks the
     * spread constraint heuristic.
     *
     * @returns An integer of the x position of the most left segment
     */
    @Override
    public int getMostLeftPoint() {
        int mostLeftPoint = Integer.MAX_VALUE;
        for (Segment segment : skyline) {
            if (segment.start.x < mostLeftPoint) {
                mostLeftPoint = segment.start.x;
            }
        }
        return mostLeftPoint;
    }

    @Override
    public boolean testSpreadConstraint(Rectangle rectangle) {
        return false;
    }

    @Override
    public int getFitnessNumber(Rectangle rectangle) {
        return 0;
    }

    @Override
    public boolean onlyFit(Rectangle rectangle) {
        return false;
    }

    @Override
    public int getLocalWaste(Rectangle rectangle) {
        return 0;
    }

    /**
     * Places a rectangle and modifies the skyline accordingly.
     *
     * @param rectangle the Rectangle to place
     */
    public void addRectangle(Rectangle rectangle) {
        // Note that a rectangle is always placed with the origin matching a skyline point.
        Segment segmentOnWhichIsPlaced = null;
        int index = 0;
        for (Segment segment : skyline) {
            if (segment.start.equals(rectangle) || segment.end.equals(rectangle)) {
                segmentOnWhichIsPlaced = segment;
                index = skyline.indexOf(segment);
            }
        }

        // Case 1: rectangle to be placed is smaller than segment on which is to be placed
        if (segmentOnWhichIsPlaced.getLength() > rectangle.height) {
            // Top left corner of rectangle is placed on upper candidate position of segment
            if (segmentOnWhichIsPlaced.start.equals(new Point(rectangle.x, rectangle.y))) {
                SegPoint endPoint = segmentOnWhichIsPlaced.end;
                skyline.remove(segmentOnWhichIsPlaced);
                skyline.add(index, new Segment(new SegPoint(true, new Point(rectangle.x + rectangle.width, rectangle.y)),
                        new SegPoint(false, new Point(rectangle.x + rectangle.width, rectangle.y + rectangle.height))));
                skyline.add(index + 1, new Segment(new SegPoint(true, new Point(endPoint.x, rectangle.y + rectangle.height + 1)), endPoint));
            } else if (segmentOnWhichIsPlaced.end.equals((new Point(rectangle.x, rectangle.y + rectangle.height)))) {
                SegPoint beginPoint = segmentOnWhichIsPlaced.start;
                skyline.remove(segmentOnWhichIsPlaced);
                skyline.add(index, new Segment(beginPoint, new SegPoint(false, new Point(beginPoint.x, rectangle.y - 1))));
                skyline.add(index + 1, new Segment(new SegPoint(true, new Point(rectangle.x + rectangle.width, rectangle.y)),
                        new SegPoint(false, new Point(rectangle.x + rectangle.width, rectangle.y + rectangle.height))));
            }
        } else if (segmentOnWhichIsPlaced.getLength() == rectangle.height) { // Case 2: rectangle to placed is exactly as big as the segment on which it is placed
            skyline.remove(segmentOnWhichIsPlaced);
            skyline.add(index, new Segment(new SegPoint(true, new Point(rectangle.x + rectangle.width, rectangle.y)), new SegPoint(false,
                    new Point(rectangle.x + rectangle.width, rectangle.y + rectangle.height))));
        } else { // Case 3 : rectangle to be placed is longer than the segment on which it is placed
            if (segmentOnWhichIsPlaced.start.equals(new Point(rectangle.x, rectangle.y))) {
                skyline.remove(segmentOnWhichIsPlaced);
                int upToThisY = rectangle.y + rectangle.height;
                skyline.add(index, new Segment(new SegPoint(true, new Point(rectangle.x + rectangle.width, rectangle.y)),
                        new SegPoint(false, new Point(rectangle.x + rectangle.width, upToThisY))));

                for (int i = index + 1; i < skyline.size() - 1; i++) {
                    if (skyline.get(i).end.y < upToThisY) {
                        skyline.remove(skyline.get(i));
                    }
                }

                // From here we only need to cut a segment in half
                Segment segmentToCut = skyline.get(index + 1);
                SegPoint upToSegPoint = segmentToCut.end;
                skyline.remove(segmentToCut);
                skyline.add(index + 1, new Segment(new SegPoint(true, new Point(upToSegPoint.x, rectangle.y + rectangle.height + 1)), upToSegPoint));
            } else if (segmentOnWhichIsPlaced.end.equals((new Point(rectangle.x, rectangle.y + rectangle.height)))) {
                skyline.remove(segmentOnWhichIsPlaced);
                int upToThisY = rectangle.y;
                skyline.add(index, new Segment(new SegPoint(true, new Point(rectangle.x + rectangle.width, upToThisY)),
                        new SegPoint(false, new Point(rectangle.x + rectangle.width, rectangle.y + rectangle.height))));

                for (int i = index - 1; i > - 1; i--) {
                    if (skyline.get(i).start.y > upToThisY) {
                        skyline.remove(skyline.get(i));
                    }
                }

                // From here we only need to cut a segment in half
                Segment segmentToCut = null;

                for (Segment segment : skyline) {
                    if (segment.start.y < upToThisY && segment.end.y > upToThisY) {
                        segmentToCut = segment;
                    }
                }

                SegPoint upToSegPoint = segmentToCut.start;
                skyline.remove(segmentToCut);
                skyline.add(skyline.indexOf(segmentToCut), new Segment(upToSegPoint, new SegPoint(false, new Point(upToSegPoint.x, rectangle.y - 1))));
            }
        }
    }
}
