package cs250.paint.PaintTools;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.io.InputStream;

//This class is extremely similar to the RectangleTool class but forces a 1:1 aspect ratio on the rectangle
/**
 * A paint tool designed to draw a square to the canvas.
 */
public class SquareTool extends PaintTool {
    private double startX, startY, endX;

    //SquareTool Events
    /**
     * Handles a user's initial mouse press for drawing a square and updates brush parameters in case they were changed.
     * @param mouseEvent
     * The MouseEvent associated with the user's click
     */
    public void onMousePressed(MouseEvent mouseEvent) {
        startX = mouseEvent.getX();
        startY = mouseEvent.getY();

        //Getting a copy of the canvas before any preview of the square can be drawn
        copyCanvas();

        //Making sure the brush has up-to date attributes
        updateBrushParameters();
    }

    /**
     * Handles the dynamic redrawing for a square
     * @param mouseEvent
     * The MouseEvent associated with the mouse drag
     */
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

    /**
     * Draws the final square to the canvas
     * @param mouseEvent
     * The MouseEvent associated with the user releasing the mouse after a click or drag
     */
    public void onMouseReleased(MouseEvent mouseEvent) {
        pasteCanvasCopy();

        endX = mouseEvent.getX();

        //Actually draw the square
        graphicsContext.strokeRect(startX, startY, endX - startX,endX - startX);

    }

    /**
     * Retrieves the square tool icon from project resources
     * @return
     * An image object containing the square tool icon.
     */
    public Image getShapeIcon() {
        InputStream resourceStream = getClass().getResourceAsStream("/cs250/paint/icons/Square.png");

        if (resourceStream == null) {
            System.out.println("Resource not found: /cs250/paint/icons/Square.png");
            return null;
        }

        return new Image(resourceStream);
    }

    /**
     * A basic toString function for the square tool
     * @return
     * The String "Square"
     */
    public String toString() {
        return "Square";
    }
}
