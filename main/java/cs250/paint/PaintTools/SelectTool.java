package cs250.paint.PaintTools;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

//The select tool selects a specific region of the canvas and holds that selection so that the
//copy and paste tools can work with it
//The tool is first used to select a region
//When it is used again it can drag the region
public class SelectTool extends PaintTool {
    //Boolean for indicating if there is a region that has been selected with the selection tool
    private boolean regionSelected;

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

    public SelectTool() {
        regionSelected = false;
        cutMode = false;

    }

    public void onMousePressed(MouseEvent mouseEvent) {
        graphicsContext.setLineDashes(PREVIEW_LINE_DASH_WIDTH);
        graphicsContext.setLineWidth(PREVIEW_LINE_WIDTH);
        graphicsContext.setStroke(PREVIEW_LINE_COLOR);

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

    public void onMouseDragged(MouseEvent mouseEvent) {
        pasteCanvasCopy(); // Revert canvas before redrawing

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

    public void onMouseReleased(MouseEvent mouseEvent) {
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

    public Image getShapeIcon() {
        return null;
    }

    private boolean isWithinSelectedRegion(double x, double y) {
        // Check if the mouse is within the selected region
        return (x >= startX && x <= endX && y >= startY && y <= endY);
    }

    public String toString() {
        return "Select Area";
    }

    public void setCutMode(boolean cutMode) {
        this.cutMode = cutMode;
    }
}