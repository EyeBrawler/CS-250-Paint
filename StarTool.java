package cs250.paint.PaintTools;

import javafx.scene.input.MouseEvent;

public class StarTool extends PaintTool {
    double[] xPoints;
    double[] yPoints;
    double starCenterX;
    double starCenterY;
    static final int NUMBER_OF_VERTICES = 10;

    public StarTool() {
        //Initializing the arrays to have a size of 10 for each point of the star
        xPoints = new double[NUMBER_OF_VERTICES];
        yPoints = new double[NUMBER_OF_VERTICES];
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        //Where the user clicks will be assigned to the star center variables
        starCenterX = mouseEvent.getX();
        starCenterY = mouseEvent.getY();

        copyCanvas();

        //Get the brush ready for making the stroke
        updateBrushParameters();

    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        //Load Copy As Preview of star may have been seen before (very likely)
        pasteCanvasCopy();

        //Calculating the coordinate for the star factoring mouse locations during the draw
        calculateStar(mouseEvent);

        //Drawing the star temporarily
        graphicsContext.strokePolygon(xPoints, yPoints, NUMBER_OF_VERTICES);

    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        //Load Copy As Preview of star may have been seen before (very likely)
        pasteCanvasCopy();

        //Calculating the coordinate for the star factoring mouse locations during the draw
        calculateStar(mouseEvent);

        //Drawing the star permanently
        graphicsContext.strokePolygon(xPoints, yPoints, NUMBER_OF_VERTICES);
    }

    public void calculateStar(MouseEvent mouseEvent) {
        //See line below for variables use
        final int SUCCESSIVE_VERTEX_ANGLE = 36;

        //Index 0 is the top vertex of the star
        xPoints[0] = mouseEvent.getX();
        yPoints[0] = mouseEvent.getY();

        double outerRadius = Math.abs(yPoints[0] - starCenterY);

        //0.5 is the ratio between the points and inner radius of the star
        double innerRadius = outerRadius * 0.5;

        //Calculating initial angle for the star
        double initialAngle = Math.atan2(yPoints[0] - starCenterY, xPoints[0] - starCenterX);

        // Angle between successive vertices (36 degrees in radians)
        //Needs to be converted from degrees to radians for calculation
        double angleStep = Math.toRadians(SUCCESSIVE_VERTEX_ANGLE);

        for (int i = 1; i < NUMBER_OF_VERTICES; i++) {
            // Determine whether the current vertex is an outer or inner vertex
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;

            // Calculate the angle for this vertex
            double angle = initialAngle + i * angleStep;

            // Calculate the x and y coordinates of the vertex
            double x = starCenterX + radius * Math.cos(angle);
            double y = starCenterY + radius * Math.sin(angle);

            // Store the vertex in the array
            //1 is added so that the top is
            xPoints[i] = x;
            yPoints[i] = y;

        }

    }

    public String toString() {
        return "Star";
    }
}
