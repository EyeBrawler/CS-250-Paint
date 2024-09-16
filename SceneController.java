package cs250.paint;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.String;
import java.util.Optional;

import static javafx.scene.paint.Color.WHITE;

public class SceneController {

    //Reference to stage
    private Stage stage;

    //The Canvas
    @FXML private Canvas canvas;

    //Flag to check if canvas has been modified
    //Exists for saving purposes
    private boolean unsavedChanges;

    //Canvas Graphics Context (Enables drawing to canvas)
    private GraphicsContext graphicsContext;

    //Current Open File
    //Contains information such as open file's path and name
    private File openFile;

    //Toolbar Items
    @FXML private ChoiceBox<PaintTool> toolChoice;
    @FXML private Spinner<Integer> toolWidthSpinner;
    @FXML private ColorPicker colorPicker;
    @FXML private CheckBox dashingCheckBox;


    //Variables to set tool width limits
    private static final int minToolWidth = 1;
    private static final int maxToolWidth = 1024;

    //The paintToolbox to manage paint tools
    PaintToolbox paintToolbox;


    //Function ran by JavaFX after the scene is loaded
    @FXML
    private void initialize() {
        //Getting the canvas' graphics context
        graphicsContext = canvas.getGraphicsContext2D();

        //Setting the canvas to white so that it can be differentiated from background and compatible with eraser.
        //Also allows for compatibility when copying canvas in specific tool operations
        graphicsContext.setFill(WHITE);
        graphicsContext.fillRect(0,0,canvas.getWidth(), canvas.getHeight());

        //Method that runs to set up the tool width spinner in the toolbar
        setupToolWidthSpinner();

        //Creating an instance of my paintToolbox class and
        //All tools need to have access to the graphics context, color picker, and tool width
        paintToolbox = new PaintToolbox(graphicsContext, colorPicker.getValue(), toolWidthSpinner.getValue());

        //Setting up toolChoice ChoiceBox for tool selection
        toolChoice.getItems().addAll(paintToolbox.getPaintTools());
        toolChoice.setOnAction(this::setTool); //Using the :: method reference operator to link method to ChoiceBox

        //To have an item selected by default, the first item in tool list is selected with the setValue method
        toolChoice.setValue(paintToolbox.getPaintTools().getFirst());

    }

    //Setter method to inject the Stage into this class
    //Called in main class
    public void setStage(Stage stage) {
        this.stage = stage;

        //Running smartSaveSetup() method
        //It allows a confirmation dialog box when the user has modified the canvas but not yet saved.
        //It is occurring here because this is the next code that runs after the initialize method.
        smartSaveSetup();
    }

    //Method for the about popup that can be found in the menu bar
    public void aboutMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Pain(t)");
        alert.setContentText("Version 0.3\nPain(t) is a less professional recreation of MS Paint" +
                "\nCreated By Sam Thyen");

        alert.showAndWait();
    }


    //Method for opening an image file.
    public void openDialog() {
        Image openedImage = null;
        boolean newImageOpened = false;

        FileChooser fileChooser = new FileChooser();

        //Filter Used for Image Extensions in file choosing window
        //Setting up the imageExtension filter (for all image types)
        FileChooser.ExtensionFilter imageExtensionFilter =
                new FileChooser.ExtensionFilter("Pain(t) Image Files", "*.jpg", "*.png", "*.bmp");
        fileChooser.setTitle("Open File");

        //Adding filter to fileChooser to enable sorting by image
        fileChooser.getExtensionFilters().addAll(imageExtensionFilter);

        //Trying to Select a File
        openFile = fileChooser.showOpenDialog(stage);

        //Try Catch is to handle any errors that occur in opening the image.
        if(openFile != null) {
            try {
                //Creating FileInputStream from the file.
                FileInputStream inputStream = new FileInputStream(openFile);
                //Using that stream to try to construct an image object
                openedImage = new Image(inputStream);

                newImageOpened = true;

            } catch (Exception e) {
                //This code creates an error alert box for user notification
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("File Not Found Error");
                alert.setContentText("There was an error loading the selected file as an image.");

                alert.showAndWait();
            }
        }

        if(newImageOpened) {
            //Resizing Canvas to properly fit new image
            canvas.setHeight(openedImage.getHeight());
            canvas.setWidth(openedImage.getWidth());

            //Drawing Image to graphics context
            graphicsContext.drawImage(openedImage, 0, 0);
        }

    }

    public void saveAsDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");

        //Creating file extension filters specifically for saving individual file types
        FileChooser.ExtensionFilter pngExtensionFilter =
                new FileChooser.ExtensionFilter("PNG File", "*.png");
        FileChooser.ExtensionFilter jpgExtensionFilter =
                new FileChooser.ExtensionFilter("JPG File", "*.jpg");
        FileChooser.ExtensionFilter bmpExtensionFilter =
                new FileChooser.ExtensionFilter("BMP File", "*.bmp");

        //Adding filters just created for choosing a file of a specific extension
        fileChooser.getExtensionFilters().addAll(pngExtensionFilter, jpgExtensionFilter, bmpExtensionFilter);

        //Loading the file chooser and having it try to return a file for saving to a file object
        openFile = fileChooser.showSaveDialog(stage);

        //Calling the saveCanvas() method if a file for saving has been selected.
        if(openFile != null) {
            saveCanvas();
        }
    }


    public void saveCanvas() {
        //Forcing Save As Dialog when there is no openFile to write over
        //Allows the user to pick a file extension
        //Also prevents not knowing image type for saving.

        if(openFile == null) {
            saveAsDialog();

        } else {
            String fileName = openFile.getName();
            String fileExtension = getFileExtension(fileName);

            //WritableImage is the image format the screenshot function uses
            //Taking a screenshot of the canvas is how the image will be saved
            WritableImage writableImage = canvas.snapshot(null,null);

            //Buffered Images have a type, this type will determine if imageIO can work with it.
            //ARGB is for PNG
            //RGB is for JPG and BMP
            //It is likely created originally with ARGB
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

            //Convert to correct type by redrawing if not png
            if(!"png".equals(fileExtension)) {
                //Creating the temporary buffered image needed for redrawing
                BufferedImage tempBufferedImage = new BufferedImage(bufferedImage.getWidth(),
                        bufferedImage.getHeight(),
                        BufferedImage.TYPE_INT_RGB);

                //Redrawing with Graphics2D
                Graphics2D tempGraphics = tempBufferedImage.createGraphics();
                tempGraphics.drawImage(bufferedImage, 0, 0, null);
                tempGraphics.dispose();

                bufferedImage = tempBufferedImage;
            }

            //Checking if the fileExtension exists and then writing the file using the buffered image
            if(fileExtension != null) {
                try {
                    // Write the image to the file using the appropriate format
                    ImageIO.write(bufferedImage, fileExtension, openFile);
                } catch (IOException e) {
                    //Displaying Error Message when writing fails
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR");
                    alert.setHeaderText("File Failed to Save");
                    alert.setContentText("There was an error saving the canvas to an image file.");

                    alert.showAndWait();
                }
            }
        }
    }


    //Static Method for getting a file name extension from a file name string
    private static String getFileExtension(String fileName) {
        //Searching for character with last dot and saving its spot
        int dotIndex = fileName.lastIndexOf(".") + 1;

        //Checking if dot is in a valid position (no weirdness with hidden files and files that end with ".")
        if(dotIndex > 0 && dotIndex < fileName.length() - 1) {
            //Extracting the valid extension as lowercase
            return fileName.substring(dotIndex).toLowerCase();
        } else {
            //In case no extension was found
            return null;
        }
    }

    //This function creates a custom dialog box where the user can resize the canvas.
    public void resizeCanvas() {
        //Creating a custom dialog box for this
        //It uses the pair class to store two double values, the whole key pair thing doesn't make to much sense
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

    //Method to change the active tool based on the toolChoice ChoiceBox
    public void setTool(ActionEvent event) {
        paintToolbox.setActiveTool(toolChoice.getValue(), colorPicker.getValue(), toolWidthSpinner.getValue(), dashingCheckBox.isSelected());
    }

    //Setting up the spinner for tool width
    //It requires the value factory to actually have tool width numbers inside the spinner
    private void setupToolWidthSpinner() {
        //Ranges for the spinner are set to the constant variables for tool width limits
        SpinnerValueFactory<Integer> widthValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(minToolWidth, maxToolWidth);

        //The Default width before user makes any changes
        widthValueFactory.setValue(10);

        //Linking the factory and spinner together
        toolWidthSpinner.setValueFactory(widthValueFactory);

        //Using a ChangeListener to track when the spinner is changed and update the tool width
        toolWidthSpinner.valueProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                paintToolbox.getActiveTool().setToolWidth(toolWidthSpinner.getValue());
            }
        });

    }

    //Method for updating the active tool's color when the colorPicker chooses a different color
    public void setColor() {
        paintToolbox.getActiveTool().setToolColor(colorPicker.getValue());
    }

    public void dashingCheckToggled() {
        //If the box is checked
        //Runs if the box is not checked
        paintToolbox.getActiveTool().setLineDashing(dashingCheckBox.isSelected());

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
    }

    public void easterEgg() {
        try {
            //Using the FXML loader to load the special FXML file
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("SpecialScene.fxml"));

            //Doing some basic setup with the new window
            Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
            Stage stage = new Stage();

            stage.setTitle("This is Java");
            stage.setScene(scene);

            //Passing the newly created stage to the SpecialSceneController class
            SpecialSceneController specialController = fxmlLoader.getController();
            specialController.setStage(stage);

            stage.show();

        }
        catch (IOException e) {
            //Displaying Error Message in the GUI
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Window Opening Failed");
            alert.setContentText("Special easter egg window experienced an error loading.");

            alert.showAndWait();
        }
    }

    public void smartSaveSetup() {
        //Setting the stage's new close behavior for smart saving
        stage.setOnCloseRequest(event -> {
            if (unsavedChanges) {
                event.consume();  // Stop the default close behavior

                //Constructing a confirmation alert
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Save Changes?");
                alert.setHeaderText("You have attempted to exit Pain(t) without saving.");
                alert.setContentText("Would you like to save before closing?");


                alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

                Optional<ButtonType> result = alert.showAndWait();

                //result should always be present
                //If not, something is very wrong
                if (result.isPresent()) {

                    //Dialog Box Outcomes
                    if (result.get() == ButtonType.YES) {
                        //user chooses OK
                        saveCanvas();
                        stage.close();
                        Platform.exit();
                        System.exit(0);

                    } else if (result.get() == ButtonType.NO) {
                        // ... user chose CANCEL or closed the dialog
                        //Stay in the application
                        stage.close();
                        Platform.exit();
                        System.exit(0);

                    } else {
                        event.consume();
                    }
                }

            } else {
                //What we do if the user has saved. (close Pain(t)
                stage.close();
            }
        });
    }

    public void quitPaint() {
        //Simulating the closing of the window by firing an event
        //Will allow the smartSave method to still run.
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

}
