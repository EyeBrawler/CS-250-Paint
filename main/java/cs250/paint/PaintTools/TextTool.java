package cs250.paint.PaintTools;

import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Optional;

public class TextTool extends PaintTool {

    //Variables to store the positions for the rectangle that text will be filled in
    double startX, startY, endX, endY;

    //Attributes for the preview rectangle
    private static final double PREVIEW_LINE_DASH_WIDTH = 5;
    private static final double PREVIEW_LINE_WIDTH = 1;
    private static final Color PREVIEW_LINE_COLOR = Color.BLUE;

    private static final double TEXT_OFFSET_HEIGHT = 20;
    private static final String FONT_FAMILY = "Times New Roman";

    public void onMousePressed(MouseEvent mouseEvent) {
        // As the user drags, update the endpoint
        startX = mouseEvent.getX();
        startY = mouseEvent.getY();

        copyCanvas();

        //Setting up the preview rectangle for the text
        graphicsContext.setLineDashes(PREVIEW_LINE_DASH_WIDTH);
        graphicsContext.setLineWidth(PREVIEW_LINE_WIDTH);
        graphicsContext.setStroke(PREVIEW_LINE_COLOR);
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        //Revert the canvas before the next preview rectangle is drawn
        pasteCanvasCopy();

        //Get the end mouse values as the user drags the mouse
        endX = mouseEvent.getX();
        endY = mouseEvent.getY();

        //Draw the rectangle temporarily
        graphicsContext.strokeRect(startX, startY, endX - startX, endY - startY);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        //Revert the canvas so that the rectangle is only shown temporarily for user aid
        pasteCanvasCopy();

        // Finalize the rectangle and prompt for text input
        endX = mouseEvent.getX();
        endY = mouseEvent.getY();

        // Create a dialog to input text
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Text");
        dialog.setHeaderText("Enter text to insert:");
        dialog.setContentText("Text:");

        //Run the dialog and store it in the optional object result
        Optional<String> result = dialog.showAndWait();

        //If there was a result, write the text near the rectangle
        result.ifPresent(text -> {
            //Making and setting a font
            Font currentFont = new Font(FONT_FAMILY, toolWidth);
            graphicsContext.setFont(currentFont);

            graphicsContext.setFill(toolColor); // Set text color
            graphicsContext.fillText(text, startX, startY + TEXT_OFFSET_HEIGHT); // Draw text near the start point
        });

    }

    public Image getShapeIcon() {
        return null;
    }

    public String toString() {
        return "Text";
    }
}
