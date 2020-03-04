import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Bottom-up solver
 * <p>
 *     both for rotation and no rotation.
 *     This algorithm is very similar to the first fit solver, but it tries to fit
 *     rectangles into already filled bins.
 * </p>
 */
//todo: look into the slack variable mentioned in paper
//todo: currently, it will spread over the whole height even if it causes ugly gaps. This doesn't cause a worse
//todo: result as far as I am aware, but it is a bit ugly
public class BottomUpSolver extends AbstractSolver {

    @Override
    Set<Util.HeightSupport> getHeightSupport() {
        return new HashSet<>(Arrays.asList(Util.HeightSupport.FIXED));
    }

    boolean animate = true;

    @Override
    @SuppressWarnings("Duplicates")
    Solution pack(Parameters parameters) {
        // Start with all rectangles rotated so that width >= height.
        if (parameters.rotationVariant) {
            for (Rectangle rectangle :
                    parameters.rectangles) {
                if (rectangle.height > rectangle.width) {
                    rectangle.rotate();
                }
            }
        }

        // Sort on width, with height as the tie-breaker
        parameters.rectangles.sort((o1, o2) -> (o2.height) - (o1.height));
        parameters.rectangles.sort((o1, o2) -> (o2.width) - (o1.width));

        ArrayList<Box> boxes = new ArrayList<>();
        //up until this point, the code was identical to the first fit solver, here it diverges

        ArrayList<Rectangle> toPlace = new ArrayList<>(parameters.rectangles);

        int xPos = 0; //starting x position for the next box
        while(!toPlace.isEmpty()) {
            Rectangle first = toPlace.get(0);
            toPlace.remove(0);

            //if this was the last rectangle, rotate it to minimize width
            if(toPlace.isEmpty()) {
                if(first.width > first.height && first.width <= parameters.height) {
                    first.rotate();
                }
            }

            Box box = new Box(first,xPos, parameters.height, parameters.rotationVariant);
            boxes.add(box);

            xPos += box.width; //the width of the first box

            if(!toPlace.isEmpty()) {
                packRun(box, toPlace);
            }
        }

        return new Solution(parameters, this);
    }

    /**
     * Packs as many rectangles as possible into a single box.
     * Updates the toPlace list.
     * @param box the box to fill
     * @param toPlace the rectangles that still need to be filled
     */
    private void packRun(Box box, ArrayList<Rectangle> toPlace) {
        ArrayList<Rectangle> toRemove = new ArrayList<>();

        // place the largest width rectangle that fits in the remaining height
        for(Rectangle rectangle : toPlace) {
            if( box.heightFilled >= box.height) {
                break; //will no longer fit anything
            }

            if( box.heightFilled + rectangle.height <= box.height) {
                toRemove.add(rectangle);
                box.firstPassPlace(rectangle, box.heightFilled);
                box.heightFilled += rectangle.height;
            }
        }
        toPlace.removeAll(toRemove); //todo: I am sure there are better ways to do this, I could do a removeIf with the placed variable

        //merge rows together that have the same remaining width
        box.mergeRows();

        //final row should be extended to reach the max height
        Row finalRow = box.rows.get(box.rows.size() -1);
        finalRow.height = box.height - finalRow.yPos;

        //now we need an list of all rectangles to go sorted on area
        //todo: I now make a second array for this, however, it might be faster to just resort the toGo array later, unsure
        ArrayList<Rectangle> areaSorted = new ArrayList<>();
        areaSorted.addAll(toPlace);
        areaSorted.sort((o1, o2) -> (o2.height * o2.width) - (o1.height * o1.width));

        //keep finding the row with the most remaining width
        //place the largest area rectangle that fits
        while( box.rows.size() > 1) {
            box.rows.sort((o1, o2) -> (o2.widthLeft) - (o1.widthLeft)); //todo after the first sort only the just altered rows will be changed, faster way?
            Row row = box.rows.get(0);
            toRemove = new ArrayList<>(); //todo: this continues to not be a great way of doing this
            boolean placedAny = false;

            for( Rectangle rectangle : areaSorted) {
                if( rectangle.width <= row.widthLeft && rectangle.height <= row.height) {
                    box.place(rectangle, row);
                    placedAny = true;
                    toRemove.add(rectangle);
                    break;
                } else if (box.rotation && rectangle.height <= row.widthLeft && rectangle.width <= row.height) {
                    rectangle.rotate();
                    box.place(rectangle, row);
                    placedAny = true;
                    toRemove.add(rectangle);
                    break;
                }
            }
            if (placedAny) {
                areaSorted.removeAll(toRemove);
                toPlace.removeAll(toRemove);
            } else {
                //pick the neighbouring row with the most space left
                Row toMerge;
                if( row.next == null || (row.previous != null && row.previous.widthLeft >= row.next.widthLeft)) {
                    toMerge = row.previous;
                } else {
                    toMerge = row.next;
                }

                toMerge.height += row.height;
                if( row.yPos < toMerge.yPos) {
                    toMerge.yPos = row.yPos;
                }

                box.rows.remove(row);
            }
            box.mergeRows();
        }
    }

    /**
     * A box represents a single pack run.
     * its height is determined by the fixed height,
     * the width by the width of the first rectangle placed in it.
     * The width and height do not change anymore.
     */
    private class Box {
        int height;
        int width;
        int xPos;
        ArrayList<Row> rows = new ArrayList<>();
        boolean rotation;
        int heightFilled = 0;

        /**
         * Constructor
         * @param first the first rectangle to be placed in the box.
         * @param x the x coordinate the box starts at
         * @param height the given fixed height.
         * @param rotation whether rotation is allowed or not.
         */
        Box(Rectangle first, int x, int height, boolean rotation) {
            this.height = height;
            this.xPos = x;
            this.width = first.width;
            this.rotation = rotation;
            this.heightFilled += first.height;
            first.x = x;
            first.y = 0;
            rows.add(new Row(first, this, null, null));
            first.place(true);
        }

        void firstPassPlace(Rectangle rectangle, int y) {
            rectangle.add(rectangle);
            rectangle.x = xPos;
            rectangle.y = y;
            rectangle.place(true);
            Row previous = rows.get(rows.size() - 1);
            Row row = new Row(rectangle, this, previous, null);
            rows.add(row);
            previous.next = row;
        }

        void place(Rectangle rectangle, Row row) {
            rectangle.x = row.xPos + (width - row.widthLeft);

            //put it up against the neighbouring row with the least width left, or the edge of the box if possible
            int y = row.yPos;
            if( row.next == null || (row.previous != null && row.previous.widthLeft > row.next.widthLeft)) {
                y += row.height - rectangle.height;
            }

            rectangle.y = y;
            rectangle.place(true);

            if( rectangle.height == row.height) {
                row.widthLeft -= rectangle.width;
            } else {
                Row previous;
                Row next;
                Row newRow = new Row(rectangle, this, row.widthLeft);

                if( y == row.yPos) { //our new row starts where the old row started
                    previous = row.previous;
                    next = row;
                    row.yPos += rectangle.height; // old row is shifted down
                    if( row.yPos + row.height > row.box.height) {
                        row.height = row.box.height - row.yPos;
                    }
                    row.previous = newRow;
                } else {
                    previous = row;
                    next = row.previous;
                    row.next = newRow;
                }

                newRow.previous = previous;
                newRow.next = next;
                row.height -= rectangle.height; //old row loses height
                rows.add(newRow);
            }
            mergeRows(); //todo: this might be slow, but I don't think there's any way around this
        }

        void mergeRows(){
            ArrayList<Row> toRemove = new ArrayList<>(); //todo: again, must be better way, removeIf height = 0?
            for( Row row : rows) {
                if( row.previous == null) {
                    continue;
                }

                if( row.widthLeft == row.previous.widthLeft) {
                    row.previous.height += row.height;
                    if( row.previous.height + row.previous.yPos > row.box.height) {
                        row.previous.height = row.box.height - row.previous.yPos;
                    }
                    row.height = 0;
                    if(row.next != null) {
                        row.next.previous = row.previous;
                        row.previous.next = row.next;
                    } else {
                        //row.previous.next = null; //todo this causes a nullpointer exception to occur somewhere and I don't know where or why
                    }
                    toRemove.add(row);
                }
            }
            rows.remove(toRemove);
        }

    }

    /**
     * A row represents the remaining space to fill in a horizontal slice of a box.
     * All rectangles in a row are also in the box the row belongs to.
     * A row has a reference to the box it belongs to.
     */
    private class Row {
        int xPos;
        int yPos;
        int widthLeft;
        int height;
        Box box;
        Row previous;
        Row next;

        /**
         * Constructor when creating a new row from a single rectangle
         * @param first
         */
        Row(Rectangle first, Box box, Row previous, Row next) {
            this.box = box;
            this.xPos = first.x;
            this.yPos = first.y;
            this.height = first.height;
            this.widthLeft = box.width - first.width;
            this.previous = previous;
            this.next = next;
        }

        Row(Rectangle first, Box box, int widthLeft) {
            this.box = box;
            this.xPos = first.x;
            this.yPos = first.y;
            this.height = first.height;
            this.widthLeft = widthLeft - first.width;
        }

    }
}
