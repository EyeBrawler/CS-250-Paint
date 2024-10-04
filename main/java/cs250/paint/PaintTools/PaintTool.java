package cs250.paint.PaintTools;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

//The PaintTools class simply holds all the code for tools contained in the toolChoice ChoiceBox and tools
//within toggle buttons
public abstract class PaintTool {

    //To ensure that these components can be accessed by all paint tools, but not too much else, they are protected
    protected GraphicsContext graphicsContext; //Way of interacting with canvas
    protected Color toolColor;
    protected int toolWidth;
    protected boolean lineDashing;
    protected Image shapeIcon;

    //A writable image object used for making copies of the canvas
    //Allows actively seeing shapes/lines before they are drawn
    protected WritableImage copiedCanvasImage;

    //The PaintTool uses the zero parameter constructor when no icon needs to be stored in the class
    // all tool attributes are updated during drawing, not when the PaintTool is initialized.

    public void setToolColor(Color toolColor) {
        this.toolColor = toolColor;
    }

    public void setToolWidth(int toolWidth) {
        this.toolWidth = toolWidth;
    }

    public void setLineDashing(boolean lineDashing) {
        this.lineDashing = lineDashing;
    }

    public void setGraphicsContext(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    //Method to update the toolColor and toolWidth for a brush
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

    public void pasteCanvasCopy() {
        graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(),
                graphicsContext.getCanvas().getHeight()); // Clear the canvas to prevent weird accumulation, still fuzzy

        //Frequently for restoring the backed up canvas before user draws a preview line/shape
        graphicsContext.drawImage(copiedCanvasImage,0,0);
    }

    //Mouse interactions that will need to be implemented in all tools
    public abstract void onMousePressed(MouseEvent mouseEvent);

    public abstract void onMouseDragged(MouseEvent mouseEvent);

    public abstract void onMouseReleased(MouseEvent mouseEvent);

    /**
     * Method to retrieve the icon used for a shape tool.
     * @return
     * An image object for containing the shape tool icon.
     */
    public abstract Image getShapeIcon();

    public abstract String toString();

}

