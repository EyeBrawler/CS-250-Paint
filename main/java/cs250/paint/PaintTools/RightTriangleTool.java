package cs250.paint.PaintTools;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.io.InputStream;

/**
 * A paint tool designed to draw a right triangle to the canvas.
 */
public class RightTriangleTool extends PaintTool {
    private static final int NUMBER_OF_POINTS = 3;

    double[] xPoints;
    double[] yPoints;

    /**
     * Constructs a right triangle tool
     */
    public RightTriangleTool() {
        //Initializing the arrays to have a size of 3 for each point of the triangle
        xPoints = new double[NUMBER_OF_POINTS];
        yPoints = new double[NUMBER_OF_POINTS];
    }

    /**
     * Handles a user's initial mouse press for drawing a right triangle and ensuring the tool's attributes are up to
     * date upon a user's click.
     * @param mouseEvent
     * The MouseEvent associated with the user's click
     */
    public void onMousePressed(MouseEvent mouseEvent) {
        //Getting values for the first point on click
        xPoints[0] = mouseEvent.getX();
        yPoints[0] = mouseEvent.getY();

        //Copying canvas to allow previewing triangle
        copyCanvas();

        updateBrushParameters();
    }

    /**
     * Handles the dynamic redrawing for a right triangle
     * @param mouseEvent
     * The MouseEvent associated with the mouse drag
     */
    public void onMouseDragged(MouseEvent mouseEvent) {
        //Pasting Canvas just in the situation onMousePressed() just ran
        pasteCanvasCopy();

        //Function to calculate points for the right triangle
        calculateRightTriangle(mouseEvent.getX(), mouseEvent.getY());

        //Temporarily draw the triangle
        graphicsContext.strokePolygon(xPoints, yPoints, 3);

    }

    /**
     * Draws the final right triangle to the canvas
     * @param mouseEvent
     * The MouseEvent associated with the user releasing the mouse after a click or drag
     */
    public void onMouseReleased(MouseEvent mouseEvent) {
        //This method does the same things as onMouseDragged(), it is just what is run finally
        // in the case that the user never dragged the mouse

        //Pasting Canvas just in the situation onMousePressed() just ran
        pasteCanvasCopy();

        //Function to calculate points for the right triangle
        calculateRightTriangle(mouseEvent.getX(), mouseEvent.getY());

        //Permanently draw the triangle
        graphicsContext.strokePolygon(xPoints, yPoints, NUMBER_OF_POINTS);

    }

    /**
     * A method too calculate the coordinate location for the different points of the right triangle being drawn. For
     * the purposes of unit testing, this method is public.
     * @param currentX
     * The X coordinate of the vertex opposite the one chosen by the user on initial click
     * @param currentY
     * The Y coordinate of the vertex opposite the one chosen by the user on initial click
     */
    public void calculateRightTriangle(double currentX, double currentY) {
        //Second Point of the Triangle is where the user clicks
        xPoints[1] = currentX;
        yPoints[1] = currentY;

        //Second Point is where the right angle for the triangle will be
        //Achieved by taking the x of the first point and y of the second point
        xPoints[2] = xPoints[0];
        yPoints[2] = yPoints[1];
    }

    /**
     * Retrieves the right triangle tool icon from project resources
     * @return
     * An image object containing the right triangle tool icon.
     */
    public Image getShapeIcon() {
        InputStream resourceStream = getClass().getResourceAsStream("/cs250/paint/icons/RightTriangle.png");

        if (resourceStream == null) {
            System.out.println("Resource not found: /cs250/paint/icons/RightTriangle.png");
            return null;
        }

        return new Image(resourceStream);
    }

    /**
     * A basic toString function for the right triangle tool
     * @return
     * The String "Right Triangle"
     */
    public String toString() {
        return "Right Triangle";
    }
}
