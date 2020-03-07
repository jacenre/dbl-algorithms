import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Bottom-up solver
 * <p>
 *     both for rotation and no rotation.
 *     This algorithm is very similar to the first fit solver, but it tries to fit
 *     rectangles into already filled bins, keeping rotation options in mind.
 * </p>
 */
//todo: look into the slack variable mentioned in paper
//todo: just an idea, but maybe when picking the first rectangle for a box, you could check if the next rectangle has height <= width of the rectangle. Then you could rotate it I think
//todo: or instead try to rotate all rectangles in the last box
public class BottomUpSolver extends AbstractSolver {

    @Override
    Set<Util.HeightSupport> getHeightSupport() {
        return new HashSet<>(Arrays.asList(Util.HeightSupport.FIXED, Util.HeightSupport.FREE));
    }

    @Override
    public boolean canSolveParameters(Parameters parameters) {
        boolean superResult = super.canSolveParameters(parameters);
        if (!superResult) return false;
        if (parameters.rectangles.size() > 2000 && (
                parameters.heightVariant == Util.HeightSupport.FREE || parameters.freeHeightUtil)) return false;
        return true;
    }

    Parameters parameters;

    @Override
    @SuppressWarnings("Duplicates")
    Solution pack(Parameters parameters) {
        this.parameters = parameters;
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

        //up until this point, the code was identical to the first fit solver, here it diverges

        ArrayList<Rectangle> toPlace = new ArrayList<>(parameters.rectangles);

        int xPos = 0; //starting x position for the next box
        while (!toPlace.isEmpty()) {
            Rectangle first = toPlace.get(0);
            toPlace.remove(0);

            //if this was the last rectangle, rotate it to minimize width
            if(toPlace.isEmpty()) {
                if(first.width > first.height && first.width <= parameters.height) {
                    if (parameters.rotationVariant) first.rotate();
                }
            }

            Box box = new Box(first, xPos, parameters.height, parameters.rotationVariant);

            xPos += box.width; //the width of the first box

            if (!toPlace.isEmpty()) {
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
        for (Rectangle rectangle : toPlace) {
            if (box.heightFilled >= box.height) {
                break; //will no longer fit anything
            }

            if (box.heightFilled + rectangle.height <= box.height) {
                toRemove.add(rectangle);
                box.firstPassPlace(rectangle);
            }
        }
        toPlace.removeAll(toRemove);

        //if needed, add final row to fit last bit of height. This will always be merged with the one before it
        if (box.heightFilled != box.height) {
            Row finalRow = box.rows.get(box.rows.size() - 1);
            Row newRow = new Row(box, finalRow);
            box.rows.add(newRow);
            finalRow.next = newRow;
        }

        //merge rows together that have the same remaining width
        box.mergeRows();

        //now we need an list of all rectangles to go sorted on area
        ArrayList<Rectangle> areaSorted = new ArrayList<>(toPlace);
        areaSorted.sort((o1, o2) -> (o2.height * o2.width) - (o1.height * o1.width));

        //keep finding the row with the most remaining width
        //place the largest area rectangle that fits
        while (box.rows.size() >= 1) { //the border row is not considered a row
            box.rows.sort((o1, o2) -> (o2.widthLeft) - (o1.widthLeft));
            Row row = box.rows.get(0);
            toRemove = new ArrayList<>();
            boolean placedAny = false;

            for (Rectangle rectangle : areaSorted) {
                if( rectangle.width <= row.widthLeft && rectangle.height <= row.height) {
                    box.place(rectangle, row);
                    placedAny = true;
                    toRemove.add(rectangle);
                    break;
                } else if (box.rotation && rectangle.height <= row.widthLeft && rectangle.width <= row.height) {
                    if (parameters.rotationVariant) rectangle.rotate();
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
                if (box.rows.size() == 1) {
                    break;
                }

                //pick the neighbouring row with the most space left
                Row toMerge;
                if(row.previous.widthLeft >= row.next.widthLeft) {
                    toMerge = row.previous;
                    toMerge.next = row.next;
                } else {
                    toMerge = row.next;
                    toMerge.previous = row.previous;
                }

                toMerge.height += row.height;
                if (row.yPos < toMerge.yPos) {
                    toMerge.yPos = row.yPos;
                }

                row.height = 0;
                row.widthLeft = 0;
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
        Row border = new Row(this);

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
            rows.add(new Row(first, this, border, border));
            first.place(true);
            Util.animate(parameters, getSolver());
        }

        void firstPassPlace(Rectangle rectangle) {
            rectangle.x = xPos;
            rectangle.y = heightFilled;
            heightFilled += rectangle.height;
            rectangle.place(true);
            Row previous = rows.get(rows.size() - 1); //only works because this is before row sorting
            Row row = new Row(rectangle, this, previous, border);
            rows.add(row);
            previous.next = row;
            Util.animate(parameters, getSolver());
        }

        void place(Rectangle rectangle, Row row) {
            rectangle.x = row.xPos;

            //put it up against the neighbouring row with the least width left, or the edge of the box if possible
            int yPos = row.yPos;
            if (row.previous.widthLeft > row.next.widthLeft) {
                yPos += row.height - rectangle.height; //place it against the next row instead
            }

            rectangle.y = yPos;
            rectangle.place(true);
            Util.animate(parameters, getSolver());

            //if the placed rectangle overlaps with the row perfectly, this could be done more efficiently.
            //not currently doing that
            Row previous;
            Row next;
            Row newRow = new Row(rectangle, this, row.widthLeft);

            if (rectangle.y == row.yPos) { //our new row starts where the old row started
                previous = row.previous;
                next = row;
                row.yPos += rectangle.height; // old row is shifted down
                row.previous = newRow;
            } else {
                previous = row;
                next = row.next;
                row.next = newRow;
            }

            newRow.previous = previous;
            newRow.next = next;
            row.height -= rectangle.height; //old row loses height
            rows.add(newRow);
            mergeRows();
        }

        void mergeRows(){
            ArrayList<Row> toRemove = new ArrayList<>();
            for (Row row : rows) {
                if (row.previous == border) {
                    continue;
                }

                if (row.widthLeft == row.previous.widthLeft) {
                    row.previous.height += row.height;
                    row.height = 0;
                    row.widthLeft = 0; //not having this causes overlaps and I don't know why
                    row.previous.next = row.next;
                    if (row.next != border) {
                        row.next.previous = row.previous;
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
         * @param first the first rectangle placed in this row
         * @param box the box this row is for
         * @param next the row below this one, border if there is no row below
         * @param previous the row above this one, border if there is no row above
         */
        Row(Rectangle first, Box box, Row previous, Row next) {
            this.box = box;
            this.xPos = first.x + first.width;
            this.yPos = first.y;
            this.height = first.height;
            this.widthLeft = box.width - first.width;
            this.previous = previous;
            this.next = next;
        }

        Row(Rectangle first, Box box, int widthLeft) {
            this.box = box;
            this.xPos = first.x + first.width;
            this.yPos = first.y;
            this.height = first.height;
            this.widthLeft = widthLeft - first.width;
        }

        Row(Box box, Row previous) {
            this.box = box;
            this.xPos = previous.xPos;
            this.yPos = box.heightFilled;
            this.height = box.height - box.heightFilled;
            box.heightFilled += this.height;
            this.previous = previous;
            this.next = box.border;
            this.widthLeft = previous.widthLeft;
        }

        Row(Box box) { //used only for the border, has no previous or next
            this.widthLeft = 0;
            this.height = 0;
            this.box = box;
            xPos = box.xPos;
            yPos = 0;
        }

    }

    public AbstractSolver getSolver() {
        return this;
    }
}
