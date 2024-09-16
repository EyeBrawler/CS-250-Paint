package cs250.paint;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class EllipseTool extends PaintTool {
    //upperLeftX is the upper left of the ellipse and where the user starts dragging
    double upperLeftX, upperLeftY, endX, endY;

    EllipseTool(GraphicsContext graphicsContext, Color toolColor, int toolWidth) {
        super(graphicsContext, toolColor, toolWidth);
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        upperLeftX = mouseEvent.getX();
        upperLeftY = mouseEvent.getY();

        //Getting a copy of the canvas before any preview of the ellipse can be drawn
        copyCanvas();

        //Making sure the brush has the correct attributes
        updateBrushParameters();
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        //Reloading the canvas copy
        //Important if the mouse has already been dragged
        pasteCanvasCopy();

        //Getting new end coordinates
        endX = mouseEvent.getX();
        endY = mouseEvent.getY();

        //The end position for the cursor is the bottom right corner (actually outside the ellipse itself)
        graphicsContext.strokeOval(upperLeftX, upperLeftY, endX - upperLeftX, endY - upperLeftY);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        pasteCanvasCopy();

        endX = mouseEvent.getX();
        endY = mouseEvent.getY();

        //Actually draw the ellipse on mouse release
        graphicsContext.strokeOval(upperLeftX, upperLeftY, endX - upperLeftX, endY - upperLeftY);

    }

    public String toString() {
        return "Ellipse";
    }
}
