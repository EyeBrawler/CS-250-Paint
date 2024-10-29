package cs250.paint.PaintTools;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

/**
 * A paint tool designed for the freehand drawing of a line.
 */
public class PencilTool extends PaintTool {

    /**
     * Runs when the user clicks with the pencil tool. Sets up the tool parameters for freehand drawing and begins a
     * stroke path.
     * @param mouseEvent
     * The MouseEvent associated with the user's click
     */
    public void onMousePressed(MouseEvent mouseEvent) {
        //Making sure the brush has updated color and width information
        updateBrushParameters();

        //Resetting the line path for a new line
        graphicsContext.beginPath();

    }

    /**
     * Adds a segment to the path based on the mouse's new position and draws in that new segment with parameters used
     * by all other paint tools.
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
     * Closes the stroke path and fills in the area in which the user clicked (especially important if they only clicked
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
     * An unused method that would otherwise provide a pencil icon. This method is not used because toggle button icons
     * are handled by scene builder.
     * @return
     * Normally an Image object, in this case, null
     */
    public Image getShapeIcon() {
        return null;
    }

    /**
     * A basic toString function for the pencil tool
     * @return
     * The String "Pencil"
     */
    public String toString() {
        return "Pencil";
    }
}
