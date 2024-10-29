package cs250.paint.PaintTools;

import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Optional;

/**
 * A paint tool for writing text to the canvas. The user can select a rectangular region where they would like to insert
 * text and then a dialog box will pop up allowing the user to enter any text.
 */
public class TextTool extends PaintTool {

    //Variables to store the positions for the rectangle that text will be filled in
    double startX, startY, endX, endY;

    //Attributes for the preview rectangle
    private static final double PREVIEW_LINE_DASH_WIDTH = 5;
    private static final double PREVIEW_LINE_WIDTH = 1;
    private static final Color PREVIEW_LINE_COLOR = Color.BLUE;

    private static final double TEXT_OFFSET_HEIGHT = 20;
    private static final String FONT_FAMILY = "Times New Roman";

    /**
     * Stores the initial points for the dashed rectangle indicating where the user would like to type text. This is the
     * canvas location where they initially click.
     * @param mouseEvent
     * The MouseEvent associated with the user's click
     */
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

    /**
     * Live draws the preview rectangle based upon where the user drags the mouse
     * @param mouseEvent
     * The MouseEvent associated with the mouse drag
     */
    public void onMouseDragged(MouseEvent mouseEvent) {
        //Revert the canvas before the next preview rectangle is drawn
        pasteCanvasCopy();

        //Get the end mouse values as the user drags the mouse
        endX = mouseEvent.getX();
        endY = mouseEvent.getY();

        //Draw the rectangle temporarily
        graphicsContext.strokeRect(startX, startY, endX - startX, endY - startY);
    }

    /**
     * Pops up a text input dialog box where the user can type text they would like to insert on the canvas.
     * @param mouseEvent
     * The MouseEvent associated with the user releasing the mouse after a click or drag
     */
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

    /**
     * An unused method that would otherwise provide an icon for the text tool. This method is not used because toggle
     * button icons are handled by scene builder.
     * @return
     * Normally an Image object, in this case, null
     */
    public Image getShapeIcon() {
        return null;
    }

    /**
     * A basic toString function for the text tool
     * @return
     * The String "Text"
     */
    public String toString() {
        return "Text";
    }
}
