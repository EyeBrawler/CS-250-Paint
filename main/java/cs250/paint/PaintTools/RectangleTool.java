package cs250.paint.PaintTools;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.io.InputStream;

/**
 * A paint tool designed to draw a rectangle to the canvas by clicking and dragging in a lower right direction.
 */
public class RectangleTool extends PaintTool {
    private double startX, startY, endX, endY;

    /**
     * Handles a user's initial mouse press for drawing a rectangle
     * @param mouseEvent
     * The MouseEvent associated with the user's click
     */
    public void onMousePressed(MouseEvent mouseEvent) {
        startX = mouseEvent.getX();
        startY = mouseEvent.getY();

        //Getting a copy of the canvas before any preview of the rectangle can be drawn
        copyCanvas();

        //Making sure stroke has the most up-to date attributes
        updateBrushParameters();
    }

    /**
     * Handles the dynamic redrawing for a rectangle
     * @param mouseEvent
     * The MouseEvent associated with the mouse drag
     */
    public void onMouseDragged(MouseEvent mouseEvent) {
        //Reloading the canvas copy
        //Important if the mouse has already been dragged
        pasteCanvasCopy();

        //Getting new end coordinates
        endX = mouseEvent.getX();
        endY = mouseEvent.getY();

        graphicsContext.strokeRect(startX, startY, endX - startX, endY - startY);

    }

    /**
     * Draws the final rectangle to the canvas
     * @param mouseEvent
     * The MouseEvent associated with the user releasing the mouse after a click or drag
     */
    public void onMouseReleased(MouseEvent mouseEvent) {
        pasteCanvasCopy();

        //Getting mouse release coordinates
        endX = mouseEvent.getX();
        endY = mouseEvent.getY();

        //Actually draw the rectangle
        graphicsContext.strokeRect(startX, startY, endX - startX, endY - startY);

    }

    /**
     * Retrieves the rectangle tool icon from project resources
     * @return
     * An image object containing the rectangle tool icon.
     */
    public Image getShapeIcon() {
        InputStream resourceStream = getClass().getResourceAsStream("/cs250/paint/icons/Rectangle.png");

        if (resourceStream == null) {
            System.out.println("Resource not found: /cs250/paint/icons/Rectangle.png");
            return null;
        }

        return new Image(resourceStream);
    }

    /**
     * A basic toString function for the rectangle tool
     * @return
     * The String "Rectangle"
     */
    public String toString() {
        return "Rectangle";
    }
}
