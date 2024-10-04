package cs250.paint.PaintTools;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.io.InputStream;

//This class is extremely similar to the RectangleTool class but forces a 1:1 aspect ratio on the rectangle
public class SquareTool extends PaintTool {
    private double startX, startY, endX;

    //SquareTool Events
    public void onMousePressed(MouseEvent mouseEvent) {
        startX = mouseEvent.getX();
        startY = mouseEvent.getY();

        //Getting a copy of the canvas before any preview of the square can be drawn
        copyCanvas();

        //Making sure the brush has up-to date attributes
        updateBrushParameters();
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        //Reloading the canvas copy
        //Important if the mouse has already been dragged
        pasteCanvasCopy();

        //Getting new end coordinates
        //The Y does not matter because a square is being drawn
        endX = mouseEvent.getX();

        //Draw the preview square
        graphicsContext.strokeRect(startX, startY, endX - startX,endX - startX);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        pasteCanvasCopy();

        endX = mouseEvent.getX();

        //Actually draw the square
        graphicsContext.strokeRect(startX, startY, endX - startX,endX - startX);

    }

    public Image getShapeIcon() {
        InputStream resourceStream = getClass().getResourceAsStream("/cs250/paint/icons/Square.png");

        if (resourceStream == null) {
            System.out.println("Resource not found: /cs250/paint/icons/Square.png");
            return null;
        }

        return new Image(resourceStream);
    }

    public String toString() {
        return "Square";
    }
}
