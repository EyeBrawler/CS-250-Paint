package cs250.paint.PaintTools;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.io.InputStream;

public class EllipseTool extends PaintTool {
    //upperLeftX is the upper left of the ellipse and where the user starts dragging
    double upperLeftX, upperLeftY, endX, endY;

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

    public Image getShapeIcon() {
        InputStream resourceStream = getClass().getResourceAsStream("/cs250/paint/icons/Ellipse.png");

        if (resourceStream == null) {
            System.out.println("Resource not found: /cs250/paint/icons/Ellipse.png");
            return null;
        }

        return new Image(resourceStream);
    }

    public String toString() {
        return "Ellipse";
    }
}
