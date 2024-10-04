package cs250.paint.PaintTools;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.io.InputStream;

public class StarPolygonTool extends PaintTool{
    //Arrays to store point values
    private double[] xPoints;
    private double[] yPoints;

    //The center point of the polygon
    //Will be where the user starts drawing
    private double centerX, centerY;
    private double firstX, firstY;

    //Variable to store the number of the star will have
    private int numberOfPoints;

    public void onMousePressed(MouseEvent mouseEvent) {
        //Resetting the arrays after each click as their size can change
        xPoints = new double[numberOfPoints * 2];
        yPoints = new double[numberOfPoints * 2];

        updateBrushParameters();

        //The mouse position on click is the center of the polygon
        centerX = mouseEvent.getX();
        centerY = mouseEvent.getY();

        copyCanvas(); //For live draw
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        pasteCanvasCopy();

        //Get the mouse coordinates during the mouse drag
        firstX = mouseEvent.getX();
        firstY = mouseEvent.getY();

        //Calculate the points
        calculateStarPolygon();

        //Temporarily draw the polygon
        graphicsContext.strokePolygon(xPoints, yPoints, numberOfPoints * 2);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        pasteCanvasCopy();

        calculateStarPolygon();

        //Permanently draw the triangle
        graphicsContext.strokePolygon(xPoints, yPoints, numberOfPoints * 2);

    }

    private void calculateStarPolygon() {
        // Calculate the radius from the center to the first outer point
        double outerRadius = Math.sqrt(Math.pow(firstX - centerX, 2) + Math.pow(firstY - centerY, 2));

        // Calculate the initial angle (the angle between the center and the first point)
        double angleOffset = Math.atan2(firstY - centerY, firstX - centerX);

        // Calculate the angle between the points
        double angleIncrement = Math.PI / numberOfPoints; // Star polygons alternate between outer and inner points

        // Inner radius for the star (adjustable depending on how "deep" you want the star)
        double innerRadius = outerRadius * 0.5; // This can be customized for different star shapes

        // Loop through each point of the star
        for (int i = 0; i < numberOfPoints * 2; i++) {
            double radius = (i % 2 == 0) ? outerRadius : innerRadius; // Use outer for even indices and inner for odd
            double currentAngle = angleOffset + i * angleIncrement;
            xPoints[i] = centerX + radius * Math.cos(currentAngle);
            yPoints[i] = centerY + radius * Math.sin(currentAngle);
        }
    }

    public void setNumberOfPoints(int numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    public Image getShapeIcon() {
        InputStream resourceStream = getClass().getResourceAsStream("/cs250/paint/icons/StarPolygon.png");

        if (resourceStream == null) {
            System.out.println("Resource not found: /cs250/paint/icons/StarPolygon.png");
            return null;
        }

        return new Image(resourceStream);
    }

    public String toString() {
        return "Star Polygon";
    }
}
