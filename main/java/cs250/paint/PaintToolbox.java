package cs250.paint;

import cs250.paint.PaintTools.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;

/**
 * The Purpose of this class is to store all the paint tools and swap between them,
 * It is also the place where instances of shape tools of different types are constructed
 */
public class PaintToolbox {

    //List all shape paint tools are stored in
    private final ArrayList<PaintTool> shapePaintTools;

    private PaintTool activeTool;

    /**
     * Initializing a PaintToolbox by creating paint tools for drawing shapes and adding them to a list of
     * shapePaintTools.
     */
    public PaintToolbox() {
        //Toolbox Setup
        shapePaintTools = new ArrayList<>();

        //Shapes
        shapePaintTools.add(new RectangleTool());
        shapePaintTools.add(new SquareTool());
        shapePaintTools.add(new EllipseTool());
        shapePaintTools.add(new CircleTool());
        shapePaintTools.add(new TriangleTool());
        shapePaintTools.add(new RightTriangleTool());
        shapePaintTools.add(new StarTool());
        shapePaintTools.add(new PolygonTool());
        shapePaintTools.add(new StarPolygonTool());

    }

    //Self Explanatory Getters and Setters
    /**
     * Method to get the currently active PaintTool
     * @return
     * A PaintTool object that is the active PaintTool
     */
    public PaintTool getActiveTool() {
        return activeTool;
    }

    /**
     * Sets the active PaintTool and all of its attributes.
     * @param activeTool
     * The tool that will be made active.
     * @param graphicsContext
     * A GraphicsContext the tool will draw on.
     * @param toolColor
     * A Color object representing the color the active tool will use.
     * @param toolWidth
     * An integer value specifying the line thickness/width the active tool will have.
     * @param lineDashing
     * Whether the new active tool will have line dashing enabled or disabled.
     */
    public void setActiveTool(PaintTool activeTool, GraphicsContext graphicsContext, Color toolColor, int toolWidth,
                              boolean lineDashing) {
        this.activeTool = activeTool;

        //Tool color and width are brought in from the scene controller to update these attributes even when the
        //user has never touched the color or width options in the program.
        this.activeTool.setToolColor(toolColor);
        this.activeTool.setToolWidth(toolWidth);
        this.activeTool.setLineDashing(lineDashing);
        this.activeTool.setGraphicsContext(graphicsContext);

    }

    /**
     * Returns a list of PaintTools that are for drawing shape (and therefore found within the shape tool combo
     * box)
     * @return
     * A list of PaintTools that are for drawing shapes.
     */
    public List<PaintTool> getShapeTools() {
        return shapePaintTools;
    }

}
