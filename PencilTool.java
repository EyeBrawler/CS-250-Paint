package cs250.paint.PaintTools;

import javafx.scene.input.MouseEvent;

public class PencilTool extends PaintTool {

    public void onMousePressed(MouseEvent mouseEvent) {
        //Making sure the brush has updated color and width information
        updateBrushParameters();

        //Resetting the line path for a new line
        graphicsContext.beginPath();

    }

    public void onMouseDragged(MouseEvent mouseEvent) {

        //Adding a segment of the line to the path based on new mouse position
        graphicsContext.lineTo(mouseEvent.getX(), mouseEvent.getY());

        //Stroking the line each time the cursor is dragged, prevents gaps
        graphicsContext.stroke();

    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        //Stroking again just in case the user only clicked
        graphicsContext.stroke();

        //Closing the path so that there are no issues when switching to another tool
        graphicsContext.closePath();

    }

    public String toString() {
        return "Pencil";
    }
}
