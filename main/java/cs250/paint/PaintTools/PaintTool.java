package cs250.paint.PaintTools;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * The PaintTool class contains code used by all PaintTools this includes abstract methods for mouse usage, updating
 * tool attributes and storing fields all tools need access to (like a graphics context for drawing)
 */
public abstract class PaintTool {

    //To ensure that these components can be accessed by all paint tools, but not too much else, they are protected
    /**
     * Way for a Paint Tool to interact with the canvas
     */
    protected GraphicsContext graphicsContext;

    /**
     * The current color of a PaintTool
     */
    protected Color toolColor;

    /**
     * The line width/thickness of the paint tool
     */
    protected int toolWidth;

    /**
     * Whether a paint tool is set to dash its lines.
     */
    protected boolean lineDashing;

    /**
     * A writable image object used for making copies of the canvas
     * Allows actively seeing shapes/lines before they are drawn
     */
    protected WritableImage copiedCanvasImage;

    //The PaintTool uses the zero parameter constructor when no icon needs to be stored in the class
    // all tool attributes are updated during drawing, not when the PaintTool is initialized.

    /**
     * Set the color to be used by all paint tools
     * @param toolColor
     * A color object whose color will be applied to the tools
     */
    public void setToolColor(Color toolColor) {
        this.toolColor = toolColor;
    }

    /**
     * Sets the width of all paint tools
     * @param toolWidth
     * An integer specifying the thickness (or size) of a stroke made by any type of PaintTool
     */
    public void setToolWidth(int toolWidth) {
        this.toolWidth = toolWidth;
    }

    /**
     * Sets the boolean value for whether strokes made by any tool will applicable will have dashed lines
     * @param lineDashing
     * A boolean set too true for line dashing and false for no line dashing
     */
    public void setLineDashing(boolean lineDashing) {
        this.lineDashing = lineDashing;
    }

    /**
     * Set the GraphicsContext that all tools will draw to.
     * @param graphicsContext
     * The GraphicsContext tools will draw to.
     */
    public void setGraphicsContext(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    /**
     * Updates the current graphics context to use the current toolColor, toolWidth, and line dashing (or no line
     * dashing)
     */
    public void updateBrushParameters() {
        graphicsContext.setStroke(toolColor);
        graphicsContext.setLineWidth(toolWidth);

        //Conditional to update line dashing
        if(lineDashing) {
            graphicsContext.setLineDashes(50);
        } else {
            graphicsContext.setLineDashes((double[]) null);
        }
    }

    /**
     * Makes a copy of the canvas and stores it in the WritableImage copiedCanvasImage
     */
    public void copyCanvas() {
        //Used for Dynamically drawing shapes/lines
        //Similar to save as code, making a snapshot of the canvas and copying it
        // so there is a backup before line drawn.
        //
        // The backup will be rewritten after each preview

        //Snapshot parameters needed to properly configure canvas screenshot
        //The copiedCanvasImage will be used to restore the backup
        SnapshotParameters params = new SnapshotParameters();
        copiedCanvasImage = graphicsContext.getCanvas().snapshot(params, null);
    }

    /**
     * Draws the copiedCanvasImage to the canvas
     */
    public void pasteCanvasCopy() {
        graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(),
                graphicsContext.getCanvas().getHeight()); // Clear the canvas to prevent weird accumulation, still fuzzy

        //Frequently for restoring the backed up canvas before user draws a preview line/shape
        graphicsContext.drawImage(copiedCanvasImage,0,0);
    }

    //Mouse interactions that will need to be implemented in all tools are the below three methods
    /**
     * Method that will handle the initial mouse click for a PaintTool
     * @param mouseEvent
     * The MouseEvent associated with the user's click
     */
    public abstract void onMousePressed(MouseEvent mouseEvent);

    /**
     * Method that will handle the mouse dragging for a PaintTool
     * @param mouseEvent
     * The MouseEvent associated with the mouse drag
     */
    public abstract void onMouseDragged(MouseEvent mouseEvent);

    /**
     * Method that will handle when the user releases a click or drag for a paint tool.
     * @param mouseEvent
     * The MouseEvent associated with the user releasing the mouse after a click or drag
     */
    public abstract void onMouseReleased(MouseEvent mouseEvent);

    /**
     * Method to retrieve the icon used for a shape tool.
     * @return
     * An image object for containing the shape tool icon.
     */
    public abstract Image getShapeIcon();

    /**
     * A toString method to be implemented by all PaintTools
     * @return
     * The name of a paint tool. Usually excludes "tool" in its name
     */
    public abstract String toString();
    //This method is usually used when getting text for combo box labels
}

