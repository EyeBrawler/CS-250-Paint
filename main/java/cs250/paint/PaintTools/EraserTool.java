package cs250.paint.PaintTools;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import static javafx.scene.paint.Color.WHITE;

/**
 * A paint tool designed for freehand erasing of the canvas. It gets rid of color by changing regions of the canvas to
 * white (and not transparent).
 */
public class EraserTool extends PaintTool {

    /**
     * Runs when the user clicks with the eraser tool. Sets up the tool parameters for erasing and begins a path.
     * @param mouseEvent
     * The MouseEvent associated with the user's click
     */
    public void onMousePressed(MouseEvent mouseEvent) {
        //Eraser will get rid of color by changing anything to white
        //Transparent is not acceptable in this case because the canvas is white and not transparent
        graphicsContext.setStroke(WHITE);

        //Manually updating eraser width
        graphicsContext.setLineWidth(toolWidth);

        //Manually setting telling the eraser tool to not have any dashes
        graphicsContext.setLineDashes((double[]) null);

        //Resetting the line path for a new line of erasing
        graphicsContext.beginPath();

    }

    //This method works in the exact same way as it does in the pencil class
    /**
     * Adds a segment to the path based on the mouse's new position and erases that new segment.
     * @param mouseEvent
     * The MouseEvent associated with the mouse drag
     */
    public void onMouseDragged(MouseEvent mouseEvent) {
        //Adding a segment of the line to the path based on new mouse position
        graphicsContext.lineTo(mouseEvent.getX(), mouseEvent.getY());

        //Stroking the line each time the cursor is dragged, prevents gaps
        graphicsContext.stroke();
    }

    /**
     * Closes the stroke path and erases the area in which the user clicked (especially important if they only clicked
     * and did not drag).
     * @param mouseEvent
     * The MouseEvent associated with the user releasing the mouse after a click or drag
     */
    public void onMouseReleased(MouseEvent mouseEvent) {
        //Stroking again just in case the user only clicked
        graphicsContext.stroke();

        //Closing the path so that there are no issues when switching to another tool
        graphicsContext.closePath();
    }

    /**
     * An unused method that would otherwise provide an eraser icon. This method is not used because toggle button icons
     * are handled by scene builder.
     * @return
     * Normally an Image object, in this case, null
     */
    public Image getShapeIcon() {
        return null;
    }

    /**
     * A basic toString function for the eraser tool
     * @return
     * The String "Eraser"
     */
    public String toString() {
        return "Eraser";
    }
}
