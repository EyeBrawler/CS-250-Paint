package cs250.paint;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class LineTool extends PaintTool{
    private double startX, startY, endX, endY;

    LineTool(GraphicsContext graphicsContext, Color toolColor, int toolWidth) {
        super(graphicsContext, toolColor, toolWidth);
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        startX = mouseEvent.getX();
        startY = mouseEvent.getY();

        //Getting a copy of the canvas before any preview of the line can be drawn
        copyCanvas();

        //Setting the color and brush width
        updateBrushParameters();
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        //Reloading the canvas copy
        //Important if the mouse has already been dragged
        pasteCanvasCopy();

        //Getting new end coordinates
        endX = mouseEvent.getX();
        endY = mouseEvent.getY();

        //Drawing the preview line
        graphicsContext.strokeLine(startX, startY, endX, endY);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        //Loading the canvas copying case the mouse was last dragged
        //prevents seeing any preview lines
        pasteCanvasCopy();

        //Getting final end coordinates
        //This line is necessary in case the user never dragged the mouse during line creation
        endX = mouseEvent.getX();
        endY = mouseEvent.getY();

        //Drawing the final line
        graphicsContext.strokeLine(startX, startY, endX, endY);
    }

    public String toString() {
        return "Line";
    }

}
