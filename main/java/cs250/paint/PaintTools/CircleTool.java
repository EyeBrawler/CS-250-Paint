package cs250.paint.PaintTools;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.io.InputStream;

/**
 * A paint tool designed for drawing a circle to the canvas.
 */
public class CircleTool extends PaintTool {
    //upperLeftX is the upper left of the ellipse and where the user starts dragging
    private double upperLeftX, upperLeftY, endX;

    /**
     * Handles a user's initial mouse press for drawing a circle
     * @param mouseEvent
     * The MouseEvent associated with the user's click
     */
    public void onMousePressed(MouseEvent mouseEvent) {
        upperLeftX = mouseEvent.getX();
        upperLeftY = mouseEvent.getY();

        //Getting a copy of the canvas before any preview of the circle can be drawn
        copyCanvas();

        //Ensuring the tool has the proper attributes
        updateBrushParameters();
    }

    /**
     * Handles the dynamic redrawing for a circle
     * @param mouseEvent
     * The MouseEvent associated with the mouse drag
     */
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

    /**
     * Draws the circle in its final position to the canvas
     * @param mouseEvent
     * The MouseEvent associated with the user releasing the mouse after a click or drag
     */
    public void onMouseReleased(MouseEvent mouseEvent) {
        pasteCanvasCopy();

        endX = mouseEvent.getX();

        //Actually draw the circle
        graphicsContext.strokeOval(upperLeftX, upperLeftY, endX - upperLeftX, endX - upperLeftX);

    }

    /**
     * Retrieves the circle tool icon
     * @return
     * An image object for containing the circle tool icon.
     */
    public Image getShapeIcon() {
        InputStream resourceStream = getClass().getResourceAsStream("/cs250/paint/icons/Circle.png");

        if (resourceStream == null) {
            System.out.println("Resource not found: /cs250/paint/icons/Circle.png");
            return null;
        }

        return new Image(resourceStream);
    }

    /**
     * A basic toString function for the circle tool
     * @return
     * The String "Circle"
     */
    public String toString() {
        return "Circle";
    }
}
