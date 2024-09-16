package cs250.paint;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;

//The Purpose of this class is to store all the paint tools and swap between them,
//It is also the place where instances of tools of different types are constructed
public class PaintToolbox {

    //List all paint tools are stored in
    private final ArrayList<PaintTool> paintTools;

    private PaintTool activeTool;

    //Information that all tools in the toolbox need initially
    //Getting the color picker so current colors can be found for tools in the toolbox
    //Variables that will have values brought in from Scene Controller
    public PaintToolbox(GraphicsContext graphicsContext, Color toolColor, int toolWidth) {
        //Toolbox Setup
        paintTools = new ArrayList<>();

        //Adding various tools to the list of tools
        //Lines
        paintTools.add(new LineTool(graphicsContext, toolColor, toolWidth));
        paintTools.add(new PencilTool(graphicsContext, toolColor, toolWidth));
        paintTools.add(new EraserTool(graphicsContext, toolColor, toolWidth));

        //Shapes
        paintTools.add(new RectangleTool(graphicsContext, toolColor, toolWidth));
        paintTools.add(new SquareTool(graphicsContext, toolColor, toolWidth));
        paintTools.add(new EllipseTool(graphicsContext, toolColor, toolWidth));
        paintTools.add(new CircleTool(graphicsContext, toolColor, toolWidth));
        paintTools.add(new TriangleTool(graphicsContext, toolColor, toolWidth));
        paintTools.add(new StarTool(graphicsContext, toolColor, toolWidth));


    }

    //Self Explanatory Getters and Setters
    public PaintTool getActiveTool() {
        return activeTool;
    }

    public void setActiveTool(PaintTool activeTool, Color toolColor, int toolWidth, boolean lineDashing) {
        this.activeTool = activeTool;

        //Tool color and width are brought in from the scene controller to update these attributes even when the
        //user has never touched the color or width options in the program.
        this.activeTool.setToolColor(toolColor);
        this.activeTool.setToolWidth(toolWidth);
        this.activeTool.setLineDashing(lineDashing);

    }

    public List<PaintTool> getPaintTools() {
        return paintTools;
    }

}
