import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ArrayListSkyline extends AbstractSkyline {

    public ArrayList<Segment> skyline;
    public int globalHeight;
    public int globalWidth;
    public int maximumSpread;

    public ArrayListSkyline(int height, int width, int maximumSpread) {
        this.globalHeight = height;
        this.globalWidth = width;
        this.maximumSpread = maximumSpread;

        skyline = new ArrayList<>();
        skyline.add(new Segment(new SegPoint(true, 0, 0), new SegPoint(false, 0, height)));
    }

    @Override
    public boolean testSpreadConstraint(Rectangle rectangle, SegPoint position) {
        return position.x + rectangle.width - getMostLeftPoint() > maximumSpread;
    }

    public ArrayList<Segment> deepCopySkyline(ArrayList<Segment> skyline) {
        ArrayList<Segment> result = new ArrayList<>();
        for (Segment segment : skyline) {
            result.add(segment);
        }
        return result;
    }


    public ArrayList<Rectangle> deepCopyRectangles(ArrayList<Rectangle> sequence) {
        ArrayList<Rectangle> result = new ArrayList<>();
        for (Rectangle rec : sequence) {
            result.add(rec);
        }
        return result;
    }

    @Override
    public int getLocalWaste(Rectangle rectangle, SegPoint position, ArrayList<Rectangle> sequence) {
        ArrayList<Segment> skylineBefore = deepCopySkyline(skyline);

        // Compute wasted space left
        int areaBefore = getAreaOfSkyline(skyline);
        addRectangle(rectangle, position);
        int areaAfter = getAreaOfSkyline(skyline);
        int wastedSpaceLeft = areaAfter - areaBefore - rectangle.height * rectangle.width;

        // wasted space right
        int wastedSpaceRight = 0;
        int[] minMax = getMinWidthHeightOtherRectangles(rectangle, sequence);
        int spaceLeftRight = globalWidth - position.x - rectangle.width;
        if (minMax[0] > spaceLeftRight) {
            wastedSpaceRight = spaceLeftRight * rectangle.height;
        }

        // Get segment corresponding to right side of rectangle
        Segment segmentInQuestion = null;
        for (Segment segment : skyline) {
            if (segment.start.y == position.y || segment.end.y == position.y) {
                segmentInQuestion = segment;
            }
        }
        int index = skyline.indexOf(segmentInQuestion);

        // Wasted space above
        // TODO: dit klopt nog niet helemaal
        int wastedSpaceAbove = 0;
        while (index > 0) {
            index--;
            Segment toCheck = skyline.get(index);
            if (toCheck.start.x < segmentInQuestion.start.x && toCheck.getLength() < minMax[1]) {
                wastedSpaceAbove += (segmentInQuestion.start.x - toCheck.start.x) * toCheck.getLength();
            }
        }

        // Wasted space below
        int wastedSpaceBelow = 0;
        while (index < skyline.size() - 1) {
            index++;
            Segment toCheck = skyline.get(index);
            if (toCheck.start.x < segmentInQuestion.start.x && toCheck.getLength() < minMax[1]) {
                wastedSpaceBelow += (segmentInQuestion.start.x - toCheck.start.x) * toCheck.getLength();
            }
        }

        skyline = skylineBefore;

        return  wastedSpaceLeft + wastedSpaceBelow + wastedSpaceRight + wastedSpaceAbove;
    }


    public int[] getMinWidthHeightOtherRectangles(Rectangle rectangle, ArrayList<Rectangle> sequence) {
        int minWidth = Integer.MAX_VALUE;
        int minHeight = Integer.MAX_VALUE;
        for (Rectangle rec : sequence) {
            if (rec != rectangle) {
                if (rec.width < minWidth) {
                    minWidth = rec.width;
                }
                if (rec.height < minHeight) {
                    minHeight = rec.height;
                }
            }
        }
        return new int[] {minWidth, minHeight};
    }

    @Override
    public int getFitnessNumber(Rectangle rectangle, SegPoint position) {
        int fitnessNumber = 0;

        // Touching Right side?
        if (position.x + rectangle.width == globalWidth) {
            fitnessNumber++;
        }

        Segment segmentInQuestion = null;

        for (Segment segment : skyline) {
            if (segment.start == position || segment.end == position) {
                segmentInQuestion = segment;
            }
        }
        int index = 0;
        index = skyline.indexOf(segmentInQuestion);

        // Same as left Segment
        if (segmentInQuestion.getLength() == rectangle.height) {
            fitnessNumber++;
        }

        // Same as above
        if (index > 0 && (skyline.get(index - 1).start.x - segmentInQuestion.start.x) == rectangle.width) {
            fitnessNumber++;
        } else if (index == 0 && (globalWidth - segmentInQuestion.start.x) == rectangle.width) {
            fitnessNumber++;
        }

        //Same as below
        if (index < skyline.size() - 1 && (skyline.get(index + 1).start.x - segmentInQuestion.start.x) == rectangle.width) {
            fitnessNumber++;
        } else if (index == skyline.size() - 1 && (globalWidth - segmentInQuestion.start.x) == rectangle.width) {
            fitnessNumber++;
        }

        return fitnessNumber;
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

    /**
     * Places a rectangle and modifies the skyline accordingly.
     *
     * @param rectangle the Rectangle to place
     */
    public void addRectangle(Rectangle rectangle, SegPoint position) {
        // Note that a rectangle is always placed with the origin matching a skyline point.
        Segment segmentOnWhichIsToBePlaced = null;
        int index = 0;
        for (Segment segment : skyline) {
            if (segment.start.equals(position) || segment.end.equals(position)) {
                segmentOnWhichIsToBePlaced = segment;
                index = skyline.indexOf(segment);
            }
        }



        /* Updating the skyline */
        // Case 1: rectangle to be placed is smaller than segment on which is to be placed
        if (segmentOnWhichIsToBePlaced.getLength() > rectangle.height) {
            if (position.start) {// Top left corner of rectangle is placed on upper candidate position of segment
                SegPoint endPoint = segmentOnWhichIsToBePlaced.end;
                skyline.remove(segmentOnWhichIsToBePlaced);
                skyline.add(index, new Segment(new SegPoint(true, new Point(position.x + rectangle.width, position.y)),
                        new SegPoint(false, new Point(rectangle.x + rectangle.width, position.y + rectangle.height))));
                skyline.add(index + 1, new Segment(new SegPoint(true, new Point(endPoint.x, position.y + rectangle.height)), endPoint));
            } else if (!position.start) {// Bottom left corner of rectangle is placed on lower candidate position of segment
                SegPoint beginPoint = segmentOnWhichIsToBePlaced.start;
                skyline.remove(segmentOnWhichIsToBePlaced);
                skyline.add(index, new Segment(beginPoint, new SegPoint(false, new Point(beginPoint.x, position.y - rectangle.height))));
                skyline.add(index + 1, new Segment(new SegPoint(true, new Point(position.x + rectangle.width, position.y - rectangle.height)),
                        new SegPoint(false, new Point(position.x + rectangle.width, position.y))));
            }
        } else if (segmentOnWhichIsToBePlaced.getLength() == rectangle.height) { // Case 2: rectangle to placed is exactly as big as the segment on which it is placed
            if (position.start) {
                skyline.remove(segmentOnWhichIsToBePlaced);
                skyline.add(index, new Segment(new SegPoint(true, new Point(position.x + rectangle.width, position.y)), new SegPoint(false,
                        new Point(position.x + rectangle.width, position.y + rectangle.height))));
            } else if (!position.start) {
                skyline.remove(segmentOnWhichIsToBePlaced);
                skyline.add(index, new Segment(new SegPoint(true, new Point(position.x + rectangle.width, position.y - rectangle.height)), new SegPoint(false,
                        new Point(position.x + rectangle.width, position.y))));
            }
        } else { // Case 3 : rectangle to be placed is longer than the segment on which it is placed
            if (position.start) {
                skyline.remove(segmentOnWhichIsToBePlaced);
                int upToThisY = position.y + rectangle.height;
                skyline.add(index, new Segment(new SegPoint(true, new Point(position.x + rectangle.width, position.y)),
                        new SegPoint(false, new Point(position.x + rectangle.width, upToThisY))));

                // delete segments that are completely overshadowed
                for (int i = index + 1; i < skyline.size(); i++) {
                    if (skyline.get(i).end.y < upToThisY) {
                        skyline.remove(skyline.get(i));
                    }
                }

                // From here we only need to cut a segment in half or delete it
                Segment segmentToCut = skyline.get(index + 1);
                SegPoint upToSegPoint = segmentToCut.end;
                skyline.remove(segmentToCut);
                if (upToSegPoint.y > upToThisY) {
                    skyline.add(index + 1, new Segment(new SegPoint(true, new Point(upToSegPoint.x, position.y + rectangle.height)), upToSegPoint));
                }
            } else if (!position.start) {
                skyline.remove(segmentOnWhichIsToBePlaced);
                int upToThisY = position.y - rectangle.height;
                skyline.add(index, new Segment(new SegPoint(true, new Point(position.x + rectangle.width, upToThisY)),
                        new SegPoint(false, new Point(position.x + rectangle.width, position.y))));

                // delete segments that are completely overshadowed
                for (int i = index - 1; i > - 1; i--) {
                    if (skyline.get(i).start.y > upToThisY) {
                        skyline.remove(skyline.get(i));
                        index--;
                    }
                }

                // From here we only need to cut a segment in half or delete it
                Segment segmentToCut = skyline.get(index - 1);
                SegPoint upToSegPoint = segmentToCut.start;
                skyline.remove(segmentToCut);
                if (upToSegPoint.y < upToThisY) {
                    skyline.add(index - 1, new Segment(upToSegPoint, new SegPoint(false, new Point(upToSegPoint.x, position.y - rectangle.height))));
                }
            }
        }
    }

    @Override
    public PositionRectanglePair anyOnlyFit(ArrayList<Rectangle> sequence) {
        int[] perfectFits = new int[skyline.size()];

        for (int i = 0; i < skyline.size(); i++) {
            Rectangle rectangleThatMightBeTheOnlyFittingOne = null;
            for (Rectangle rec : sequence) {
                if (!rec.isPlaced() && skyline.get(i).getLength() == rec.height) {
                    perfectFits[i]++;
                    rectangleThatMightBeTheOnlyFittingOne = rec;
                }
            }
            if (perfectFits[i] == 1 && !testSpreadConstraint(rectangleThatMightBeTheOnlyFittingOne, skyline.get(i).start)) {
                return new PositionRectanglePair(rectangleThatMightBeTheOnlyFittingOne, skyline.get(i).start);
            }
        }
        return null;
    }

    int getAreaOfSkyline(ArrayList<Segment> skyline) {
        int total = 0;
        for (Segment segment : skyline) {
            total += segment.getLength() * segment.start.x;
        }
        return total;
    }





}
