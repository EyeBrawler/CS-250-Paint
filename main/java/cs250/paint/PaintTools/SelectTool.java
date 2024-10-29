package cs250.paint.PaintTools;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * The select tool is a paint tool that allows for the selection of a canvas region that can be moved in a subsequent
 * use of the tool. It has modes for both copying a selection and cutting one. For each use of the tool, it will
 * alternate between defining a rectangular region for selection and dragging the selected region. If no region has
 * been selected successfully during the selection defining phase, the tool will allow the user to try to define a
 * region again.
 */
public class SelectTool extends PaintTool {
    //Boolean for indicating if there is a region that has been selected with the selection tool
    private boolean regionSelected;

    //Boolean for detecting if the user dragged the mouse since it was initially pressed. Prevents an error when
    //trying to work with an area to move but no area has been selected.
    private boolean mouseDragged;

    //Boolean for indicating if we are going to cut the piece of the image out (leaving whitespace below)
    private boolean cutMode;
    private WritableImage selectedArea;

    //GrabX and grabY are the coordinates where the user grabs the selected region
    private double startX;
    private double startY;
    private double endX;
    private double endY;

    //relative grab gap is the distance between the grabbed spot and the upper left corner of the selected area
    //These values will be used to calculate the point at which the snapshot can be drawn.
    private double relativeGrabGapX;
    private double relativeGrabGapY;

    //Selection area draw point
    //the value for the top left corner of the selected area
    private double drawPointX;
    private double drawPointY;

    private static final double PREVIEW_LINE_DASH_WIDTH = 5;
    private static final double PREVIEW_LINE_WIDTH = 1;
    private static final Color PREVIEW_LINE_COLOR = Color.BLUE;

    /**
     * Constructs a SelectTool in which no region has been selected and cut mode is disabled.
     */
    public SelectTool() {
        regionSelected = false;
        cutMode = false;

    }

    /**
     * Handles either beginning a new selection or setting the place to grab an already defined selection when the user
     * first makes a mouse click.
     * @param mouseEvent
     * The MouseEvent associated with the user's click
     */
    public void onMousePressed(MouseEvent mouseEvent) {
        graphicsContext.setLineDashes(PREVIEW_LINE_DASH_WIDTH);
        graphicsContext.setLineWidth(PREVIEW_LINE_WIDTH);
        graphicsContext.setStroke(PREVIEW_LINE_COLOR);

        mouseDragged = false;

        if (regionSelected) {
            if (isWithinSelectedRegion(mouseEvent.getX(), mouseEvent.getY())) {
                // User clicked within the selected region to start dragging
                double grabX = mouseEvent.getX();
                double grabY = mouseEvent.getY();

                // Calculate the relative grab gap
                relativeGrabGapX = grabX - (Math.min(startX, endX));
                relativeGrabGapY = grabY - (Math.min(startY, endY));
            } else {
                // Reset selection if user clicked outside the selected area
                regionSelected = false;
                startX = mouseEvent.getX();
                startY = mouseEvent.getY();
            }
        } else {
            // Begin a new selection
            copyCanvas();
            startX = mouseEvent.getX();
            startY = mouseEvent.getY();
        }
    }

    /**
     * Dynamically draws a selection region or moves selected area depending upon the selection tool's state when the
     * mouse is being dragged.
     * @param mouseEvent
     * The MouseEvent associated with the mouse drag
     */
    public void onMouseDragged(MouseEvent mouseEvent) {
        pasteCanvasCopy(); // Revert canvas before redrawing
        mouseDragged = true;

        if (regionSelected) {
            // Drag the selected area relative to the mouse movement
            drawPointX = mouseEvent.getX() - relativeGrabGapX;
            drawPointY = mouseEvent.getY() - relativeGrabGapY;

            // Clear the selected region in its original location only in cutMode
            if (cutMode) {
                graphicsContext.fillRect(startX, startY, endX - startX, endY - startY);
            }

            // Draw the selected image at the new location without clearing the entire canvas
            graphicsContext.drawImage(selectedArea, drawPointX, drawPointY);

        } else {
            // If no region is selected, allow for drawing a selection rectangle
            endX = mouseEvent.getX();
            endY = mouseEvent.getY();

            // Handle negative width/height for the rectangle
            double minX = Math.min(startX, endX);
            double minY = Math.min(startY, endY);
            double width = Math.abs(endX - startX);
            double height = Math.abs(endY - startY);

            // Draw the preview selection rectangle with its calculated dimensions
            graphicsContext.strokeRect(minX, minY, width, height);
        }
    }

    /**
     * Taking a snapshot of the selected region or drawing the moved region in its final spot depending on the state
     * of the tool. Also changes the state of the tool from region selected mode to not region selected mode and vice
     * versa.
     * @param mouseEvent
     * The MouseEvent associated with the user releasing the mouse after a click or drag
     */
    public void onMouseReleased(MouseEvent mouseEvent) {
        if(mouseDragged) {
            if (regionSelected) {
                // Calculate the actual selection bounds
                double width = Math.abs(endX - startX);
                double height = Math.abs(endY - startY);

                // Update the drawing only after dragging
                if (!cutMode) {
                    graphicsContext.setFill(Color.WHITE);
                    graphicsContext.drawImage(selectedArea, drawPointX, drawPointY);
                } else {
                    graphicsContext.fillRect(startX, startY, width, height);
                    graphicsContext.drawImage(selectedArea, drawPointX, drawPointY);
                }

                // Reset region selection for future selections
                regionSelected = false;

            } else {
                // Finalize the selection rectangle and take a snapshot of the area
                endX = mouseEvent.getX();
                endY = mouseEvent.getY();

                // Calculate selection bounds
                //MinX and MinY help to find the part of the rectangle that is closed to the top left corner
                //This is essential for taking a snapshot properly
                double minX = Math.min(startX, endX);
                double minY = Math.min(startY, endY);
                double width = Math.abs(endX - startX);
                double height = Math.abs(endY - startY);

                //Only continuing if the bounds for making a selection are valid
                //Prevents error when a drag is made but the region selected has any dimensions of 0
                //This error is really just associated with odd JavaFX behavior with detecting when the mouse has been
                //dragged
                if(width > 0 && height > 0) {
                    // Draw the selection rectangle
                    graphicsContext.strokeRect(minX, minY, width, height);

                    // Take a snapshot of the area
                    SnapshotParameters selectionParameters = new SnapshotParameters();
                    selectionParameters.setViewport(new Rectangle2D(minX, minY, width, height));
                    selectedArea = new WritableImage((int) width, (int) height);
                    graphicsContext.getCanvas().snapshot(selectionParameters, selectedArea);

                    // Set start and end points
                    //The start and end points are updated to account that they need bee formatted with startX being upper left
                    //and endX being the lower right corner of the selection.
                    startX = minX;
                    startY = minY;
                    endX = minX + width;
                    endY = minY + height;

                    regionSelected = true; // Mark the region as selected
                }
            }
        }
    }

    private boolean isWithinSelectedRegion(double x, double y) {
        // Check if the mouse is within the selected region
        return (x >= startX && x <= endX && y >= startY && y <= endY);
    }

    /**
     * An unused method that would otherwise provide a select tool icon. This method is not used because toggle button
     * icons are handled by scene builder.
     * @return
     * Normally an Image object, in this case, null
     */
    public Image getShapeIcon() {
        return null;
    }

    /**
     * A basic toString function for the select tool
     * @return
     * The String "Select Area"
     */
    public String toString() {
        return "Select Area";
    }

    /**
     * Sets the select tool to run in cut mode or copy mode.
     * @param cutMode
     * True indicates cut mode will be active and false indicates copy mode will be active.
     */
    public void setCutMode(boolean cutMode) {
        this.cutMode = cutMode;
    }
}