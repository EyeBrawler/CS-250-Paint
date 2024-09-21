package cs250.paint;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.Pair;

import java.io.File;
import java.util.Optional;
import java.util.Stack;

import static javafx.scene.paint.Color.WHITE;

public class CanvasTab extends Tab{
    private final Canvas canvas;
    private final GraphicsContext graphicsContext;

    private boolean unsavedChanges = false; // Unsaved changes flag

    private static final double DEFAULT_CANVAS_WIDTH = 800;
    private static final double DEFAULT_CANVAS_HEIGHT = 600;

    private final PaintToolbox paintToolbox;
    private File openFile;

    private final Stack<WritableImage> undoStack = new Stack<>();
    private final Stack<WritableImage> redoStack = new Stack<>();

    //UndoRedoSnapshot parameters
    //These are the parameters the snapshot tool needs capture an image to store in a stack
    //They are here so that they do not have to be created over and over again
    private final SnapshotParameters undoRedoParams = new SnapshotParameters();

    public CanvasTab(PaintToolbox paintToolbox) {
        //Setting the paintToolbox parameter to the local class field paintToolbox
        this.paintToolbox = paintToolbox;

        //Creating and setting up the individual components that will go in the tab
        //Canvas Size (We are assuming the canvas will initially be blank
        canvas = new Canvas(DEFAULT_CANVAS_WIDTH, DEFAULT_CANVAS_HEIGHT);

        //StackPane is used to keep the canvas centered when it is smaller than the window
        //The canvas will be added to the stack pane
        StackPane stackPane = new StackPane(canvas);
        stackPane.setPrefHeight(0);
        stackPane.setPrefWidth(0);
        stackPane.setBackground(Background.fill(Color.LIGHTGRAY));

        //The scroll pane holds the stack pane in the case that the stack pane is larger than the window
        ScrollPane scrollPane = new ScrollPane(stackPane);

        //Configuring the scroll pane
        //These options follow the FXML file from previous version where the canvas was in the FXML
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        //Setting these values to zero ensures that the scroll pane will always follow the sizing of the tab pane
        //rather than itself
        scrollPane.prefWidth(0);
        scrollPane.prefHeight(0);

        //Passing the new CanvasTab a title for its tab and the scroll pane
        //(and therefore everything else initialized here)
        setContent(scrollPane);
        setText("New Canvas");

        // Setting event handlers for mouse actions on the canvas
        //The :: operator is used for the linking
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onCanvasMousePressed);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onCanvasMouseDragged);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, this::onCanvasMouseReleased);

        //Setting up graphics context for the canvas
        graphicsContext = canvas.getGraphicsContext2D();

        //Graphics context configuration
        graphicsContext.setFill(WHITE);
        graphicsContext.fillRect(0,0,canvas.getWidth(), canvas.getHeight());

        //Setting the graphics context to draw shapes with rounded edges
        //Prevents sharp corners from getting cut off at tight angles when drawing certain shapes
        graphicsContext.setLineJoin(StrokeLineJoin.ROUND);

        setupTabSmartSaving();

        //Add the first screenshot to the undo stack so that the user can revert back to their blank canvas
        undoStack.push(canvas.snapshot(undoRedoParams, null));

    }

    public Canvas getCanvas() {
        return canvas;
    }

    public GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }

    public File getOpenFile() {
        return openFile;
    }

    public void setOpenFile(File openFile) {
        this.openFile = openFile;
    }

    // Getter and setter for unsavedChanges flag
    public boolean hasUnsavedChanges() {
        return unsavedChanges;
    }

    public void setUnsavedChanges(boolean unsavedChanges) {
        this.unsavedChanges = unsavedChanges;
    }

    // Mouse event handling methods
    //Used for handling the start of a mouse drag
    public void onCanvasMousePressed(MouseEvent e) {
        paintToolbox.getActiveTool().onMousePressed(e);
    }

    //Handling the dragging action itself
    public void onCanvasMouseDragged(MouseEvent e) {
        paintToolbox.getActiveTool().onMouseDragged(e);
        unsavedChanges = true;
    }

    //Used for handling the end of a mouse drag
    public void onCanvasMouseReleased(MouseEvent e) {
        paintToolbox.getActiveTool().onMouseReleased(e);
        unsavedChanges = true;

        //Add snapshot to undo stack
        undoStack.push(canvas.snapshot(undoRedoParams, null));
    }

    public void undo() {
        //Checking to see if the second to last item is not null (the item we will have after we pop)
        //We don't want to try undoing when we can't
        if(undoStack.size() > 1) {
            //If we are undoing, we must add the canvas backup to the redo stack first before we pop it off
            redoStack.push(undoStack.peek());

            //Pop most recent image to get rid of it
            undoStack.pop();

            // Clear the canvas to prevent weird accumulation, still fuzzy
            graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(),
                    graphicsContext.getCanvas().getHeight());

            unsavedChanges = true;

            //Restore the top copied image
            graphicsContext.drawImage(undoStack.peek(),0,0);
        }
    }

    public void redo() {
        //Checking to see if the second to last item is not null (the item we will have after we pop)
        //We don't want to try redoing when we can't
        if(!redoStack.isEmpty()) {
            //If we are redoing, we must be able to undo our redo
            undoStack.push(redoStack.peek());

            //Pop the most recent image because it is not something that can be redone anymore
            redoStack.pop();

            // Clear the canvas to prevent weird accumulation, still fuzzy
            graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(),
                    graphicsContext.getCanvas().getHeight());

            unsavedChanges = true;

            //Restore the top copied image
            graphicsContext.drawImage(undoStack.peek(),0,0);
        }
    }

    public void clearCanvas() {
        //Constructing a confirmation alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Canvas");
        alert.setHeaderText("You are about to COMPLETELY clear the canvas.");
        alert.setContentText("Are you sure?");


        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();

        //result should always be present
        //If not, something is very wrong
        if (result.isPresent()) {

            //Dialog Box Outcomes
            if (result.get() == ButtonType.YES) {
                //They said yes, we wipe
                graphicsContext.setFill(WHITE);
                graphicsContext.fillRect(0,0,canvas.getWidth(), canvas.getHeight());

                unsavedChanges = false;
            }
            //If they say no we do nothing
        }
    }

    //This function creates a custom dialog box where the user can resize the canvas.
    public void resizeCanvas() {
        //Creating a custom dialog box for this
        //It uses the pair class to store two double values, the whole key pair thing doesn't make too much sense
        // logically but the first value is canvas width and the second is canvas height.
        Dialog<Pair<Double, Double>> resizeDialog = new Dialog<>();

        //Dialog's text
        resizeDialog.setTitle("Resize Canvas");
        resizeDialog.setHeaderText("Enter the width and height you would like to resize the canvas to.\n" +
                "WARNING: Resizing the canvas smaller will result in cropping.\n\n" +
                "Current Size: " + canvas.getWidth() + "x" + canvas.getHeight());

        //Making the resize button for the dialog
        //It must be a custom button for the text "resize"
        ButtonType resizeButtonType = new ButtonType("Resize", ButtonBar.ButtonData.OK_DONE);

        //Adding the new button and a cancel button to the dialog
        resizeDialog.getDialogPane().getButtonTypes().addAll(resizeButtonType, ButtonType.CANCEL);

        //Making textFields for the dialog box where the user can enter values
        TextField widthField = new TextField();
        widthField.setPromptText("Width");

        TextField heightField= new TextField();
        heightField.setPromptText("Height");

        //Setting up a grid layout for the dialog box to use
        GridPane gridLayout = new GridPane();
        gridLayout.setHgap(10);
        gridLayout.setVgap(10);

        //Adding the components to the layout
        gridLayout.add(new Label("Width: "),0,0);
        gridLayout.add(widthField, 1, 0);
        gridLayout.add(new Label("Height: "), 0, 1);
        gridLayout.add(heightField, 1, 1);

        //Adding the layout to the dialog window
        resizeDialog.getDialogPane().setContent(gridLayout);

        //Code to try to convert user input values and convert them to doubles
        //dialogButton is the button the user clicked
        resizeDialog.setResultConverter(dialogButton -> {
            if(dialogButton == resizeButtonType) {
                try {
                    double newWidth = Double.parseDouble(widthField.getText());
                    double newHeight = Double.parseDouble(heightField.getText());
                    return new Pair<>(newWidth, newHeight);
                } catch(NumberFormatException e) {
                    //Handling bad input from the user
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            "Please enter valid numbers for width and height.");
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });

        //Show the dialog
        Optional<Pair<Double, Double>> resizeResult = resizeDialog.showAndWait();

        //Resize the canvas
        resizeResult.ifPresent(sizeValues -> {
            double oldWidth = canvas.getWidth();
            double oldHeight = canvas.getHeight();

            canvas.setWidth(sizeValues.getKey());
            canvas.setHeight(sizeValues.getValue());

            if(oldHeight < sizeValues.getKey() || oldWidth < sizeValues.getValue()) {
                //Copy and paste the canvas will get rid of transparency
                //This will allow the new area of the canvas to be white rather than transparent so it can be seen
                paintToolbox.getActiveTool().copyCanvas();
                paintToolbox.getActiveTool().pasteCanvasCopy();
            }
        });

    }

    public void setupTabSmartSaving () {
        setOnCloseRequest(event -> {
            if (unsavedChanges) {
                event.consume();  // Stop the default close behavior

                //Constructing a confirmation alert
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Closing Tab Without Saving");
                alert.setHeaderText("You have attempted to close this tab with unsaved changes.");
                alert.setContentText("Are you sure?");


                alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

                Optional<ButtonType> result = alert.showAndWait();

                //result should always be present
                //If not, something is very wrong
                if (result.isPresent()) {

                    //Dialog Box Outcomes
                    if (result.get() == ButtonType.YES) {
                        //user chooses OK
                        Tab tab = (Tab) event.getSource(); // Get the tab from the event's source
                        getTabPane().getTabs().remove(tab);

                    } else {
                        //User selected no
                        event.consume();
                    }
                }

            } else {
                //What we do if the user has saved. (close the tab)
                Tab tab = (Tab) event.getSource(); // Get the tab from the event's source
                getTabPane().getTabs().remove(tab);

            }
        });
    }
}
