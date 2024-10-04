package cs250.paint.PaintTools;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import static javafx.scene.paint.Color.WHITE;

public class EraserTool extends PaintTool {

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

    public Image getShapeIcon() {
        return null;
    }

    public String toString() {
        return "Eraser";
    }
}
