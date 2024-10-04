package cs250.paint;

import cs250.paint.PaintTools.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;

//The Purpose of this class is to store all the paint tools and swap between them,
//It is also the place where instances of tools of different types are constructed
public class PaintToolbox {

    //List all shape paint tools are stored in
    private final ArrayList<PaintTool> shapePaintTools;

    private PaintTool activeTool;

    //Information that all tools in the toolbox need initially
    //Getting the color picker so current colors can be found for tools in the toolbox
    //Variables that will have values brought in from Scene Controller
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
    public PaintTool getActiveTool() {
        return activeTool;
    }

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

    public List<PaintTool> getShapeTools() {
        return shapePaintTools;
    }

}
