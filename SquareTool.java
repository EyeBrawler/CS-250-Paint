package cs250.paint;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

//This class is extremely similar to the RectangleTool class but forces a 1:1 aspect ratio on the rectangle
public class SquareTool extends PaintTool{
    private double startX, startY, endX, endY;

    SquareTool(GraphicsContext graphicsContext, Color toolColor, int toolWidth) {
        super(graphicsContext, toolColor, toolWidth);
    }

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
        endX = mouseEvent.getX();
        endY = mouseEvent.getY();

        //Draw the preview square
        graphicsContext.strokeRect(startX, startY, endX - startX,endX - startX);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        pasteCanvasCopy();

        endX = mouseEvent.getX();
        endY = mouseEvent.getY();

        //Actually draw the square
        graphicsContext.strokeRect(startX, startY, endX - startX,endX - startX);

    }

    public String toString() {
        return "Square";
    }
}
