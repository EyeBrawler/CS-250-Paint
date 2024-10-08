package cs250.paint.PaintTools;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.io.InputStream;


public class PolygonTool extends PaintTool {

    //Arrays to store point values
    private double[] xPoints;
    private double[] yPoints;

    //The center point of the polygon
    //Will be where the user starts drawing
    private double centerX, centerY;
    private double firstX, firstY;

    //In a regular polygon, the number of sides equals the number points
    private int numberOfSides;

    public void onMousePressed(MouseEvent mouseEvent) {
        //Resetting the arrays after each click as their size can change
        xPoints = new double[numberOfSides];
        yPoints = new double[numberOfSides];

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
        calculatePolygon();

        //Temporarily draw the polygon
        graphicsContext.strokePolygon(xPoints, yPoints, numberOfSides);

    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        pasteCanvasCopy();

        calculatePolygon();

        //Permanently draw the triangle
        graphicsContext.strokePolygon(xPoints, yPoints, numberOfSides);

    }

    //Calculating the points coordinates for the polygon
    private void calculatePolygon() {
        // Calculate the radius from the center to the first outside point
        double radius = Math.sqrt(Math.pow(firstX - centerX, 2) + Math.pow(firstY - centerY, 2));

        // Calculate the initial angle (the angle between the center and the first point)
        double angleOffset = Math.atan2(firstY - centerY, firstX - centerX);

        // Calculate the angle between each point in the polygon
        double angleIncrement = 2 * Math.PI / numberOfSides;

        // Loop through each vertex of the polygon
        for (int i = 0; i < numberOfSides; i++) {
            double currentAngle = angleOffset + i * angleIncrement;
            xPoints[i] = centerX + radius * Math.cos(currentAngle);
            yPoints[i] = centerY + radius * Math.sin(currentAngle);
        }

    }

    public void setNumberOfSides(int numberOfSides) {
        this.numberOfSides = numberOfSides;
    }

    public Image getShapeIcon() {
        InputStream resourceStream = getClass().getResourceAsStream("/cs250/paint/icons/Polygon.png");

        if (resourceStream == null) {
            System.out.println("Resource not found: /cs250/paint/icons/Polygon.png");
            return null;
        }

        return new Image(resourceStream);
    }

    public String toString() {
        return "Polygon";
    }
}
