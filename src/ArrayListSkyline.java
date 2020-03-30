import java.awt.*;
import java.util.ArrayList;

public class ArrayListSkyline extends AbstractSkyline {

    public ArrayList<Segment> skyline;
    public int globalHeight;
    public int globalWidth;
    public int maximumSpread;
    public boolean rotationsAllowed;

    public ArrayListSkyline(int height, int width, int maximumSpread, boolean rotationsAllowed) {
        this.globalHeight = height;
        this.globalWidth = width;
        this.maximumSpread = maximumSpread;
        this.rotationsAllowed = rotationsAllowed;

        skyline = new ArrayList<>();
        skyline.add(new Segment(new SegPoint(true, 0, 0), new SegPoint(false, 0, height)));
    }

    @Override
    public boolean doesNotMeetSpreadConstraint(Rectangle rectangle, SegPoint position, int mostLeftPoint) {
        return position.x + rectangle.width - mostLeftPoint > maximumSpread;
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
    public int getLocalWaste(Rectangle rectangle, SegPoint position, int[] smallestRecs) {
        ArrayList<Segment> skylineBefore = deepCopySkyline(skyline);

        // Compute wasted space left
        int areaBefore = getAreaOfSkyline(skyline);
        adjustSkyline(rectangle, position);
        int areaAfter = getAreaOfSkyline(skyline);
        int wastedSpaceLeft = areaAfter - areaBefore - rectangle.height * rectangle.width;

        // wasted space right
        int wastedSpaceRight = 0;
        int spaceLeftRight = globalWidth - position.x - rectangle.width;
        if (rectangle.width > smallestRecs[2] && smallestRecs[2] > spaceLeftRight) {
            wastedSpaceRight = spaceLeftRight * rectangle.height;
        } else if (rectangle.width == smallestRecs[2] && smallestRecs[3] > spaceLeftRight) {
            wastedSpaceRight = spaceLeftRight * rectangle.height;
        }

        // Get segment corresponding to right side of rectangle
        Segment segmentInQuestion = null;
        for (Segment segment : skyline) {
            if ((segment.start.y == position.y && segment.start.x == position.x + rectangle.width)|| (segment.end.y == position.y && segment.start.x == position.x + rectangle.width)) {
                segmentInQuestion = segment;
            }
        }
        int index = skyline.indexOf(segmentInQuestion);

        // Wasted space above
        // TODO: dit klopt nog niet helemaal
        int wastedSpaceAbove = 0;
        while (index > 0) {
//            index--;
//            Segment toCheck = skyline.get(index);
//            if (toCheck.start.x < segmentInQuestion.start.x && (rectangle.height == smallestRecs[2] ?
//                    (segmentInQuestion.start.y - toCheck.start.y) < smallestRecs[3] : (segmentInQuestion.start.y - toCheck.start.y) < smallestRecs[2])) {
//                if (index > 0) {
//                    Segment toCheckAsWell = skyline.get(index - 1);
//                    if (toCheckAsWell.end.x > toCheck.start.x) {
//                        wastedSpaceAbove += (toCheckAsWell.end.x - toCheck.start.x) * (segmentInQuestion.start.y - toCheck.start.y) ;
//                    }
//                } else {
//                    wastedSpaceAbove += (segmentInQuestion.start.x - toCheck.start.x) * toCheck.getLength();
//                }
//            }
            index--;
            Segment toCheck = skyline.get(index);
            if (toCheck.start.x < segmentInQuestion.start.x && (rectangle.height == smallestRecs[2] ?
                    toCheck.getLength() < smallestRecs[3] : toCheck.getLength() < smallestRecs[2])) {
                wastedSpaceAbove += (segmentInQuestion.start.x - toCheck.start.x) * toCheck.getLength();
            }
        }

        index = skyline.indexOf(segmentInQuestion);

        // Wasted space below
        int wastedSpaceBelow = 0;
        while (index < skyline.size() - 1) {
            index++;
            Segment toCheck = skyline.get(index);
            if (toCheck.start.x < segmentInQuestion.start.x && (rectangle.height == smallestRecs[2] ?
                    toCheck.getLength() < smallestRecs[3] : toCheck.getLength() < smallestRecs[2])) {
                wastedSpaceBelow += (segmentInQuestion.start.x - toCheck.start.x) * toCheck.getLength();
            }
        }

        skyline = skylineBefore;

        return  wastedSpaceLeft + wastedSpaceBelow + wastedSpaceRight + wastedSpaceAbove;
    }


    public int[] getMinWidthHeightOtherRectangles(ArrayList<Rectangle> sequence) {
        int minWidth = Integer.MAX_VALUE;
        int secWidth = Integer.MAX_VALUE;
        int minHeight = Integer.MAX_VALUE;
        int secHeight = Integer.MAX_VALUE;
        for (Rectangle rec : sequence) {
            if (rec.width < minWidth) {
                minWidth = rec.width;
                secWidth = minWidth;
            } else if (rec.width < secWidth) {
                secWidth = rec.width;
            }
            if (rec.height < minHeight) {
                minHeight = rec.height;
                secHeight = minHeight;
            } else if (rec.height < secHeight) {
                secHeight = rec.height;
            }
        }
//        if (rotationsAllowed) {
//            minHeight = Math.min(minHeight, minWidth);
//            minWidth = minHeight;
//        }
        return new int[] {minWidth, secWidth, minHeight, secHeight};
    }

    @Override
    public int getFitnessNumber(PositionRectangleRotationPair pair) {
        int fitnessNumber = 0;

        if (pair.rotated) {
            pair.rectangle.rotate();
        }
        // Touching Right side?
        if (pair.position.x + pair.rectangle.width == globalWidth) {
            fitnessNumber++;
        }

        Segment segmentInQuestion = null;

        for (Segment segment : skyline) {
            if (segment.start == pair.position || segment.end == pair.position) {
                segmentInQuestion = segment;
            }
        }
        int index = 0;
        index = skyline.indexOf(segmentInQuestion);

        // Same as left Segment
        if (segmentInQuestion.getLength() == pair.rectangle.height) {
            fitnessNumber++;
        }

        // Same as above
        if (index > 0 && (skyline.get(index - 1).start.x - segmentInQuestion.start.x) == pair.rectangle.width) {
            fitnessNumber++;
        } else if (index == 0 && (globalWidth - segmentInQuestion.start.x) == pair.rectangle.width) {
            fitnessNumber++;
        }

        //Same as below
        if (index < skyline.size() - 1 && (skyline.get(index + 1).start.x - segmentInQuestion.start.x) == pair.rectangle.width) {
            fitnessNumber++;
        } else if (index == skyline.size() - 1 && (globalWidth - segmentInQuestion.start.x) == pair.rectangle.width) {
            fitnessNumber++;
        }

        if (pair.rotated) {
            pair.rectangle.rotate();
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

    public void adjustSkyline(Rectangle rectangle, SegPoint position) {
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
                while(index + 1 < skyline.size()) {
                    if (skyline.get(index + 1).end.y < upToThisY) {
                        skyline.remove(skyline.get(index + 1));
                    } else {
                        break;
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

    public void mergeSegmentsNextToEachOther() {
        for (int i = 0; i < skyline.size() - 1; i++) {
            if (skyline.get(i).end.x == skyline.get(i + 1).start.x) {
                SegPoint newStart = skyline.get(i).start;
                SegPoint newEnd = skyline.get(i + 1).end;
                skyline.remove(i); skyline.remove(i);
                skyline.add(i , new Segment(newStart, newEnd));
            }
        }
        checkSkyline(skyline);
    }

    public void checkSkyline(ArrayList<Segment> skyline) {
        int totalLength = 0;
        for (Segment segment : skyline) {
            totalLength += segment.getLength();
            System.out.println(segment.start);
            System.out.println(segment.end);
        }
        System.out.println("");
        System.out.println("");
        if (totalLength != globalHeight) {

            throw new IllegalStateException();
        }
    }

    @Override
    public PositionRectangleRotationPair anyOnlyFit(ArrayList<Rectangle> rectanglesLeft, boolean rotationsAllowed) {
        int[] onlyFits = new int[skyline.size()];

        // Loop through all the segments in the skyline and check if there is a segment for which there is only one
        // rectangle left that can be placed
        for (int i = 0; i < skyline.size(); i++) {
            // If there is only one rectangle that could be placed, it will be stored in this variable
            PositionRectangleRotationPair potentialPlacement = null;
            // Loop through all the rectangles that have not been placed yet
            // TODO: test of rectangle.height == skyline.get(i).getLength() beter werkt op testcases
            for (Rectangle rectangle : rectanglesLeft) {
                if (rectangle.height <= skyline.get(i).getLength()) {
                    potentialPlacement = new PositionRectangleRotationPair(rectangle, skyline.get(i).start, false);
                    onlyFits[i]++;
                } else if (rotationsAllowed && rectangle.width <= skyline.get(i).getLength()) {
                    potentialPlacement = new PositionRectangleRotationPair(rectangle, skyline.get(i).start, true);
                    onlyFits[i]++;
                }
            }
            if (onlyFits[i] == 1 && !doesNotMeetSpreadConstraint(potentialPlacement.rectangle, skyline.get(i).start, getMostLeftPoint())) {
                if (potentialPlacement.rectangle.y + potentialPlacement.rectangle.height > globalHeight
                        || potentialPlacement.rectangle.y < 0
                        || potentialPlacement.position.x + (potentialPlacement.rotated? potentialPlacement.rectangle.height : potentialPlacement.rectangle.width) > globalWidth) {
                    return null;
                }

                return potentialPlacement;
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
