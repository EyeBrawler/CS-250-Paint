package cs250.paint;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class RectangleTool extends PaintTool{
    private double startX, startY, endX, endY;

    RectangleTool(GraphicsContext graphicsContext, Color toolColor, int toolWidth) {
        super(graphicsContext, toolColor, toolWidth);
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        startX = mouseEvent.getX();
        startY = mouseEvent.getY();

        //Getting a copy of the canvas before any preview of the rectangle can be drawn
        copyCanvas();

        //Making sure stroke has the most up-to date attributes
        updateBrushParameters();
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        //Reloading the canvas copy
        //Important if the mouse has already been dragged
        pasteCanvasCopy();

        //Getting new end coordinates
        endX = mouseEvent.getX();
        endY = mouseEvent.getY();

        graphicsContext.strokeRect(startX, startY, endX - startX, endY - startY);

        //In progress solution for getting the rectangle to draw when the mouse is dragged in any direction
        //Could be reimplemented with the strokePolygon tool instead

        //How to draw the rectangle will vary depending upon the cursor's location relative to the top
        //left corner of the rectangle. Quadrant IV can be thought of as a default because the strokeRect function
        //has its last two parameters as the coordinate for the top left of the rectangle
        /*
        if(startX < endX && startY < endY) { //Quadrant IV
            graphicsContext.strokeRect(startX, startY, endX - startX, endY - startY);
        } else if(startX < endX && startY > endY) { //Quadrant I
            graphicsContext.strokeRect(startX,startY + endY, startX, startY);
        } else if(startX > endX && startY > endY) {//Quadrant II
            graphicsContext.strokeRect(endX - startX,endY - startY, startX, startY);
        } else { //Quadrant III
            graphicsContext.strokeRect(endX - startX,endY - startY, startX, startY);
        }
         */

    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        pasteCanvasCopy();

        //Getting mouse release coordinates
        endX = mouseEvent.getX();
        endY = mouseEvent.getY();

        //Actually draw the rectangle
        graphicsContext.strokeRect(startX, startY, endX - startX, endY - startY);

    }

    public String toString() {
        return "Rectangle";
    }
}
