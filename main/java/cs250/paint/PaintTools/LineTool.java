package cs250.paint.PaintTools;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

/**
 * A paint tool designed for drawing a straight line to the canvas. It works in a click and drag fashion.
 */
public class LineTool extends PaintTool {
    private double startX, startY, endX, endY;

    /**
     * Prepares the canvas for drawing a line and stores the lines starting point.
     * @param mouseEvent
     * The MouseEvent associated with the user's click
     */
    public void onMousePressed(MouseEvent mouseEvent) {
        startX = mouseEvent.getX();
        startY = mouseEvent.getY();

        //Getting a copy of the canvas before any preview of the line can be drawn
        copyCanvas();

        //Setting the color and brush width
        updateBrushParameters();
    }

    /**
     * Handles the dynamic redrawing for the line while the user drags the cursor.
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

        //Drawing the preview line
        graphicsContext.strokeLine(startX, startY, endX, endY);
    }

    /**
     * Draws the final line to the canvas
     * @param mouseEvent
     * The mouse event associated with the user releasing the mouse button.
     */
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

    /**
     * An unused method that would otherwise provide a line tool icon. This method is not used because toggle button
     * icons are handled by scene builder.
     * @return
     * Normally an Image object, in this case, null
     */
    public Image getShapeIcon() {
        return null;
    }

    /**
     * A basic toString function for the eraser tool
     * @return
     * The String "Line"
     */
    public String toString() {
        return "Line";
    }

}
