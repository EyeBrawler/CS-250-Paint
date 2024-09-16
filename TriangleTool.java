package cs250.paint;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class TriangleTool extends PaintTool {

    double[] xPoints;
    double[] yPoints;

    TriangleTool(GraphicsContext graphicsContext, Color toolColor, int toolWidth) {
        super(graphicsContext, toolColor, toolWidth);

        //Initializing the arrays to have a size of 3 for each point of the triangle
        xPoints = new double[3];
        yPoints = new double[3];



    }

    public void onMousePressed(MouseEvent mouseEvent) {

        //Getting values for the first point on click
        xPoints[0] = mouseEvent.getX();
        yPoints[0] = mouseEvent.getY();

        //Copying canvas to allow previewing triangle
        copyCanvas();

        updateBrushParameters();
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        //Pasting Canvas just in the situation onMousePressed() just ran
        pasteCanvasCopy();

        //Function to calculate points for the right triangle
        calculateTriangle(mouseEvent);

        //Temporarily draw the triangle
        graphicsContext.strokePolygon(xPoints, yPoints, 3);

    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        //This method does the same things as onMouseDragged(), it is just what is run finally
        // in the case that the user never dragged the mouse

        //Pasting Canvas just in the situation onMousePressed() just ran
        pasteCanvasCopy();

        //Function to calculate points for the right triangle
        calculateTriangle(mouseEvent);

        //Permanently draw the triangle
        graphicsContext.strokePolygon(xPoints, yPoints, 3);

    }

    public void calculateTriangle(MouseEvent mouseEvent) {
        //Second Point of the Triangle is where the user clicks
        xPoints[1] = mouseEvent.getX();
        yPoints[1] = mouseEvent.getY();

        //Second Point is where the right angle for the triangle will be
        //Achieved by taking the x of the first point and y of the second point
        xPoints[2] = xPoints[0];
        yPoints[2] = yPoints[1];
    }

    public String toString() {
        return "Triangle";
    }
}
