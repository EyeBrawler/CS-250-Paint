package cs250.paint.PaintTools;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.io.InputStream;

public class TriangleTool extends PaintTool {
    //Arrays to store point values
    private final double[] xPoints;
    private final double[] yPoints;

    //The center point of the triangle
    //Will be where the user starts drawing
    private double centerX;
    private double centerY;

    private static final int NUMBER_OF_POINTS = 3;

    public TriangleTool() {
        xPoints = new double[NUMBER_OF_POINTS];
        yPoints = new double[NUMBER_OF_POINTS];
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        updateBrushParameters();

        //The mouse position on click is the center of the triangle
        centerX = mouseEvent.getX();
        centerY = mouseEvent.getY();

        copyCanvas(); //For live draw
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        pasteCanvasCopy();
        calculateTriangle(mouseEvent);

        //Temporarily draw the triangle
        graphicsContext.strokePolygon(xPoints, yPoints, 3);

    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        pasteCanvasCopy();
        calculateTriangle(mouseEvent);

        //Permanently draw the triangle
        graphicsContext.strokePolygon(xPoints, yPoints, NUMBER_OF_POINTS);

    }

    public Image getShapeIcon() {
        InputStream resourceStream = getClass().getResourceAsStream("/cs250/paint/icons/Triangle.png");

        if (resourceStream == null) {
            System.out.println("Resource not found: /cs250/paint/icons/Triangle.png");
            return null;
        }

        return new Image(resourceStream);
    }

    public String toString() {
        return "Triangle";
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
}
