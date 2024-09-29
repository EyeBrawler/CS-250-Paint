package cs250.paint.PaintTools;

import javafx.scene.input.MouseEvent;

public class CircleTool extends PaintTool {
    //upperLeftX is the upper left of the ellipse and where the user starts dragging
    double upperLeftX, upperLeftY, endX;

    public void onMousePressed(MouseEvent mouseEvent) {
        upperLeftX = mouseEvent.getX();
        upperLeftY = mouseEvent.getY();

        //Getting a copy of the canvas before any preview of the circle can be drawn
        copyCanvas();

        //Ensuring the tool has the proper attributes
        updateBrushParameters();
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        //Reloading the canvas copy
        //Important if the mouse has already been dragged
        pasteCanvasCopy();

        //Getting new end coordinates
        endX = mouseEvent.getX();

        //The end position for the cursor is the bottom right corner (actually outside the circle itself)
        //Drawing a circle is just like the ellipse but the width and height each use the same X values
        graphicsContext.strokeOval(upperLeftX, upperLeftY, endX - upperLeftX, endX - upperLeftX);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        pasteCanvasCopy();

        endX = mouseEvent.getX();

        //Actually draw the circle
        graphicsContext.strokeOval(upperLeftX, upperLeftY, endX - upperLeftX, endX - upperLeftX);

    }

    public String toString() {
        return "Circle";
    }
}
