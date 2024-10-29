package cs250.paint.PaintTools;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.io.InputStream;

/**
 * A paint tool designed to draw an equilateral triangle
 */
public class TriangleTool extends PaintTool {
    //Arrays to store point values
    private final double[] xPoints;
    private final double[] yPoints;

    //The center point of the triangle
    //Will be where the user starts drawing
    private double centerX;
    private double centerY;

    private static final int NUMBER_OF_POINTS = 3;

    /**
     * Constructs a triangle tool
     */
    public TriangleTool() {
        xPoints = new double[NUMBER_OF_POINTS];
        yPoints = new double[NUMBER_OF_POINTS];
    }

    /**
     * Handles the user's initial click by storing the center location of the triangle
     * @param mouseEvent
     * The MouseEvent associated with the user's click
     */
    public void onMousePressed(MouseEvent mouseEvent) {
        updateBrushParameters();

        //The mouse position on click is the center of the triangle
        centerX = mouseEvent.getX();
        centerY = mouseEvent.getY();

        copyCanvas(); //For live draw
    }

    /**
     * Handles the dynamic redrawing of the triangle around the starting center point while the user drags the mouse
     * @param mouseEvent
     * The MouseEvent associated with the mouse drag
     */
    public void onMouseDragged(MouseEvent mouseEvent) {
        pasteCanvasCopy();
        calculateTriangle(mouseEvent);

        //Temporarily draw the triangle
        graphicsContext.strokePolygon(xPoints, yPoints, 3);

    }

    /**
     * Draws the final triangle after the user release the mouse from a click.
     * @param mouseEvent
     * The MouseEvent associated with the user releasing the mouse after a click or drag
     */
    public void onMouseReleased(MouseEvent mouseEvent) {
        pasteCanvasCopy();
        calculateTriangle(mouseEvent);

        //Permanently draw the triangle
        graphicsContext.strokePolygon(xPoints, yPoints, NUMBER_OF_POINTS);

    }

    private void calculateTriangle(MouseEvent mouseEvent) {
        xPoints[0] = mouseEvent.getX();
        yPoints[0] = mouseEvent.getY();

        // Calculate the radius (distance from the center to the user's final point)
        double dx = xPoints[0] - centerX;
        double dy = yPoints[0] - centerY;

        double radius = Math.sqrt(dx * dx + dy * dy);

        // Calculate the angle from the center to the first point
        double angle = Math.atan2(dy, dx);

        // For an equilateral triangle, the angle between vertices is 120 degrees (2π/3 radians)
        double angle120 = 2 * Math.PI / 3;

        // Calculate the other two points by rotating 120 degrees around the center
        xPoints[1] = centerX + radius * Math.cos(angle + angle120);
        yPoints[1] = centerY + radius * Math.sin(angle + angle120);

        xPoints[2] = centerX + radius * Math.cos(angle - angle120);
        yPoints[2] = centerY + radius * Math.sin(angle - angle120);
    }

    /**
     * Retrieves the triangle tool icon from project resources
     * @return
     * An image object containing the triangle tool icon.
     */
    public Image getShapeIcon() {
        InputStream resourceStream = getClass().getResourceAsStream("/cs250/paint/icons/Triangle.png");

        if (resourceStream == null) {
            System.out.println("Resource not found: /cs250/paint/icons/Triangle.png");
            return null;
        }

        return new Image(resourceStream);
    }

    /**
     * A basic toString function for the triangle tool
     * @return
     * The String "Triangle"
     */
    public String toString() {
        return "Triangle";
    }


}
