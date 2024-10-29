package cs250.paint;

import cs250.paint.PaintLogger.PaintLogger;
import cs250.paint.PaintTools.*;
import cs250.paint.WebServer.PaintWebServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.io.*;

/**
 * The class which controls everything in the primary FXML file. (Essentially, everything inside Pain(t) except for
 * what is contained in the tabPane).
 */
public class SceneController {

    //Reference to stage
    private Stage stage;

    //The Tab Pane in where tabs for different open canvas' are
    @FXML
    private TabPane tabPane;

    //This value will come from the CanvasTabManager Class
    private CanvasTabManager canvasTabManager;

    //Class variable to store a fileManager object
    //The file manager class has methods for completing file operations such as loading and saving files
    private FileManager fileManager;

    //Menu Bar Nodes
    //Controls the number of sides used by the polygon tool
    @FXML
    private Spinner<Integer> polygonSideSpinner;
    private static final int MAX_POLYGON_SIDES = 9999;
    private static final int MIN_POLYGON_SIDES = 3;
    private static final int DEFAULT_POLYGON_SIDES = 3;

    @FXML
    private Spinner<Integer> starPolygonPointSpinner;
    private static final int MAX_STAR_POLYGON_POINTS = 9999;
    private static final int MIN_STAR_POLYGON_POINTS = 4;
    private static final int DEFAULT_STAR_POLYGON_POINTS = 4;

    //Toolbar Items
    //These objects control many of the paint tool attributes
    @FXML
    private ComboBox<PaintTool> shapeToolChoice;
    @FXML
    private Spinner<Integer> toolWidthSpinner;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private CheckBox dashingCheckBox;
    //Toggle Group for toolbar buttons
    //Enables the selection of one button to disable the selection of others
    //The image tool and drawing tool buttons fall into this group
    @FXML
    private ToggleGroup toolbarToggleGroup;

    //The toolbar buttons themselves
    //They are being injected into this class from the FXML so that tool objects can be added to their user data
    @FXML
    private ToggleButton selectAreaButton;
    @FXML
    private ToggleButton textInsertButton;
    @FXML
    private ToggleButton lineButton;
    @FXML
    private ToggleButton pencilButton;
    @FXML
    private ToggleButton eraserButton;

    //Check box to toggle if selection tool is in cut or copy mode
    @FXML
    private CheckMenuItem cutModeCheckBox;

    //Check box for uploading to the web server
    @FXML
    private CheckMenuItem webServerCheckBox;
    @FXML
    private CheckMenuItem autosaveNotificationCheckBox;

    //A variable for storing an instance of PaintWebServer itself
    private PaintWebServer webServer;

    //Label found in the menu bar for autosaving
    @FXML
    private Label autosaveTimerLabel;
    private AutosaveTimer autosaveTimer;

    //Variables to set tool width limits
    private static final int MIN_TOOL_WIDTH = 1;
    private static final int MAX_TOOL_WIDTH = 1024;

    private static final int DEFAULT_TOOL_WIDTH = 10;

    //The paintToolbox to manage paint tools
    private PaintToolbox paintToolbox;

    /**
     * Function ran by JavaFX after the scene is loaded
     * This contains a significant amount of setup code.
     */
    @FXML
    private void initialize() {
        //Creating an instance of my paintToolbox class
        //It stores all the paint tools and manages which one is active
        paintToolbox = new PaintToolbox();

        //Creating an instance of the CanvasTabManager class
        //It contains a reference to the tabPane and
        canvasTabManager = new CanvasTabManager(tabPane, paintToolbox);

        //Method that runs to set up the tool width spinner in the toolbar
        setupToolWidthSpinner();
        //Method for doing the same to the spinner in the shape category of the menu bar
        setupPolygonSideSpinner();

        setupStarPolygonPointSpinner();

        //Setting up the shape Tool Choice ComboBox for tool selection
        shapeToolChoice.getItems().addAll(paintToolbox.getShapeTools());

        //To have an item selected by default for the shape tools, the first item in tool list is selected
        //with the setValue method
        shapeToolChoice.setValue(paintToolbox.getShapeTools().getFirst());
        //Using the :: method reference operator to link method to ComboBox
        shapeToolChoice.setOnAction(this::setShapeTool);
        addShapeToolChoiceIcons();


        //Adding user data to the toggle buttons in the toolbar
        //Allows a tool object to be associated with a particular button
        selectAreaButton.setUserData(new SelectTool());
        textInsertButton.setUserData(new TextTool());
        lineButton.setUserData(new LineTool());
        pencilButton.setUserData(new PencilTool());
        eraserButton.setUserData(new EraserTool());

        //Setting the initial tool from the button which is currently active
        //All the parameters necessary for the tool are passed in as values
        //The first PaintTool parameter is type cast so because the user data from the toggle group returns just an
        // object.
        paintToolbox.setActiveTool((PaintTool) toolbarToggleGroup.getSelectedToggle().getUserData(),
                canvasTabManager.getActiveTab().getGraphicsContext(), colorPicker.getValue(),
                toolWidthSpinner.getValue(), dashingCheckBox.isSelected());

    }

    /**
     * A method for everything that must run directly after the scene has actually been initialized. These are
     * operations that need access to the stage (which can only be retrieved after the primary scene is created).
     */
    public void postInitialization() {
        //Running smartSaveSetup() method from the DialogManger class
        //It allows a confirmation dialog box when the user has modified the canvas but not yet saved.
        //It is occurring here because this is the next code that runs after the initialize method.
        DialogManager.smartSaveSetup(stage, canvasTabManager, fileManager);

        //Because the file manager needs access to the stage and the stage is not available until postInitialization,
        //The fileManager is initialized here
        fileManager = new FileManager(stage);
        autosaveSetup();

        //Because the autosave timer needs the fileManager and the file manager needs the autosave timer,
        //The autosave timer must be passed in separately
        fileManager.setAutosaveTimer(autosaveTimer);

        //Initializing a Paint web server object to start the web server.
        webServer = new PaintWebServer();

    }

    /**
     * Setter method to inject the Stage into this class. Called in main class.
     * @param stage
     * The main stage (which can be thought of as the main window)
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Method for the about popup that can be found in the menu bar.
     */
    public void aboutMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Pain(t)");
        alert.setContentText("Version 0.7\nPain(t) is a less professional recreation of MS Paint\nCreated By Sam Thyen");

        alert.showAndWait();
    }


    //Method for opening an image file.
    /**
     * Runs when the user clicks the open button within the file menu. Calls the DialogManager's openFile
     * method.
     */
    public void openDialog() {
        DialogManager.openFile(canvasTabManager, fileManager);
    }

    /**
     * Opens a new tab when the user clicks the "Open in a new tab" button in the file menu.
     */
    public void openNewTabDialog() {
        //Making a new tab
        canvasTabManager.newTab();

        //Opening an image in that new tab
        fileManager.openImage(canvasTabManager.getLastCanvasTab());

        //Switching to the tab where the image has been opened
        //The -2 exists because the size is always 1 larger than the max index and the last tab is the add tab button
        tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);
    }

    /**
     * Runs when the user pushes the save as button in the file menu. Calls the file manager class for this dialog.
     */
    public void saveAsDialog() {
        fileManager.saveAsDialog(canvasTabManager.getActiveTab());
    }

    /**
     * Runs when the user pushes the save button in the file menu. Calls the FileManager to save the Canvas and display
     * appropriate dialogs.
     */
    public void saveCanvas() {
        fileManager.saveCanvas(canvasTabManager.getActiveTab());
    }

    /**
     * Runs when the user pushes the "Save All Tabs" button. Calls the DialogManager for this operation.
     */
    public void saveAllTabs() {
        DialogManager.saveAllTabs(canvasTabManager, fileManager);
    }

    /**
     * Runs when the user pushes the undo button or uses its respective key binding. Calls the CanvasTabManager for this
     * operation.
     */
    public void undo() {
        canvasTabManager.getActiveTab().undo();
        PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(), "Undo Attempted");
    }

    /**
     * Runs when the user pushes the redo button or uses its respective key binding. Calls the CanvasTabManager for this
     * operation.
     */
    public void redo() {
        canvasTabManager.getActiveTab().redo();
        PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),"Redo Attempted");
    }

    /**
     * Runs when the toggle check box is pushed for disabling or enabling cutting rather than copying with the select
     * tool. Calls the PaintToolBox and CanvasTabManager
     */
    public void cutSelectionToggle() {
        if (paintToolbox.getActiveTool() instanceof SelectTool) {
            ((SelectTool) paintToolbox.getActiveTool()).setCutMode(cutModeCheckBox.isSelected());
            PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),"Cut Mode Toggled");
        }
    }

    /**
     * This function creates a custom dialog box where the user can resize the canvas.
     * See canvas tab manager for more details
     */
    public void resizeCanvas() {
        canvasTabManager.getActiveTab().resizeCanvas();
        PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),"Canvas Resize Attempted");
    }

    /**
     * Rotates the active canvas 90 degrees.
     */
    public void rotateCanvas() {
        canvasTabManager.getActiveTab().rotateCanvas();
        PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),"Canvas Rotated by 90 Degrees");
    }

    /**
     * Mirrors the active canvas horizontally.
     */
    public void mirrorCanvasHorizontally() {
        canvasTabManager.getActiveTab().mirrorCanvasHorizontally();
        PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),"Canvas Mirrored Horizontally");
    }

    /**
     * Mirrors the active canvas vertically.
     */
    public void mirrorCanvasVertically() {
        canvasTabManager.getActiveTab().mirrorCanvasVertically();
        PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),"Canvas Mirrored Vertically");
    }

    /**
     * This method executes when the "Send to Web Server" checkbox is toggled. It will change the server request
     * attribute for the
     * CanvasTab class and
     */
    public void webServerToggle() {
        canvasTabManager.getActiveTab().setServerRequest(webServerCheckBox.isSelected());

        //If we are selecting the checkbox, create a new tab with that name.
        if(webServerCheckBox.isSelected()) {
            webServer.addImagePage(canvasTabManager.getActiveTab().getOpenFile().getName(),
                    canvasTabManager.getActiveTab());

            PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),
                    "Sending to Web Server Enabled");
        } else {
            //If we are deselecting the checkbox, remove the tab with that name
            webServer.removeImagePage(canvasTabManager.getActiveTab().getOpenFile().getName());

            PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),
                    "Sending to Web Server Disabled");
        }
    }

    /**
     * This method configures the ShapeToolChoice ComboBox so that it can display icons for each tool.
     */
    public void addShapeToolChoiceIcons() {
        // Set a custom cell factory to display both text and image
        shapeToolChoice.setCellFactory(new Callback<>() {
            public ListCell<PaintTool> call(javafx.scene.control.ListView<PaintTool> param) {
                return new ListCell<>() {
                    private final ImageView imageView = new ImageView();

                    protected void updateItem(PaintTool item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            // Set the icon and the text (tool name)
                            imageView.setImage(item.getShapeIcon());
                            imageView.setFitHeight(16);  // Resize the icon if necessary
                            imageView.setFitWidth(16);
                            setText(item.toString());
                            setGraphic(imageView);
                        }
                    }
                };


            }
        });

        // Ensure the selected item shows the image and text
        shapeToolChoice.setButtonCell(shapeToolChoice.getCellFactory().call(null));
    }

    /**
     * Changes the active shape tool based on the shapeToolChoice ComboBox
     * @param event
     * The action event associated with the shape tool combo box.
     */
    private void setShapeTool(ActionEvent event) {
        paintToolbox.setActiveTool(shapeToolChoice.getValue(), canvasTabManager.getActiveTab().getGraphicsContext(),
                colorPicker.getValue(), toolWidthSpinner.getValue(), dashingCheckBox.isSelected());

        toolbarToggleGroup.selectToggle(null);

        PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),
                paintToolbox.getActiveTool().toString() + " Tool Selected");
    }

    /**
     * Used for setting the active toolbar tool. It also deselects any tool that is currently selected within the shape
     * tools
     */
    public void setToolbarTool() {
        //First checking if the there is no toggle buttons selected, in this case the tool choice will default back to
        //the selected shape
        if (toolbarToggleGroup.getSelectedToggle() == null) {
            paintToolbox.setActiveTool(shapeToolChoice.getValue(), canvasTabManager.getActiveTab().getGraphicsContext(),
                    colorPicker.getValue(), toolWidthSpinner.getValue(), dashingCheckBox.isSelected());

            PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),
                    paintToolbox.getActiveTool().toString() + " Tool Selected");

        } else {
            //Passing the tool associated with the toggle button
            //Casting the userDataObjet as a paint tool because each toggle button has a paint tool associated with it
            try {
                paintToolbox.setActiveTool((PaintTool)toolbarToggleGroup.getSelectedToggle().getUserData(),
                        canvasTabManager.getActiveTab().getGraphicsContext(), colorPicker.getValue(),
                        toolWidthSpinner.getValue(), dashingCheckBox.isSelected());

                PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),
                        paintToolbox.getActiveTool().toString() + " Tool Selected");
            } catch(Exception e) {
                System.out.println("Error: A type mismatch occurred when trying to set the active tool.");

            }
        }

    }

    /**
     * Sets up the spinner for tool width control
     */
    //It requires the value factory to actually have tool width numbers inside the spinner
    private void setupToolWidthSpinner() {
        //Ranges for the spinner are set to the constant variables for tool width limits
        SpinnerValueFactory<Integer> widthValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_TOOL_WIDTH, MAX_TOOL_WIDTH);

        //The Default width before user makes any changes
        widthValueFactory.setValue(DEFAULT_TOOL_WIDTH);

        //Linking the factory and spinner together
        toolWidthSpinner.setValueFactory(widthValueFactory);

        //Using a ChangeListener to track when the spinner is changed and update the tool width
        toolWidthSpinner.valueProperty().addListener((_, _, _) ->
                paintToolbox.getActiveTool().setToolWidth(toolWidthSpinner.getValue()));

    }

    /**
     * Sets up the spinner found in the shape section of the menu bar.
     * The spinner is for controlling the number of sides the polygon tool's polygon will have.
     */
    private void setupPolygonSideSpinner() {
        //Creating a spinner value factory with the minimum and maximum ranges available for polygon side #
        SpinnerValueFactory<Integer> sideValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_POLYGON_SIDES, MAX_POLYGON_SIDES);

        //Providing the default number of polygon sides
        sideValueFactory.setValue(DEFAULT_POLYGON_SIDES);

        polygonSideSpinner.setValueFactory(sideValueFactory);

        //Adding a listener to the spinner to update the polygon tool's number of sides
        polygonSideSpinner.valueProperty().addListener((_, _, _) -> {
            //Finding the polygon tool and setting its value to the new side value
            for(PaintTool paintTool : paintToolbox.getShapeTools()) {
                if(paintTool instanceof PolygonTool) {
                    ((PolygonTool) paintTool).setNumberOfSides(polygonSideSpinner.getValue());
                }
            }
        });

        //Making sure an initial size is set for the tool
        for(PaintTool paintTool : paintToolbox.getShapeTools()) {
            if(paintTool instanceof PolygonTool) {
                ((PolygonTool) paintTool).setNumberOfSides(polygonSideSpinner.getValue());
            }
        }
    }

    /**
     * Sets up the star polygon spinner found in the shape section of the menu bar.
     * The spinner is for controlling the number of points the star polygon tool's polygon will have.
     */
    private void setupStarPolygonPointSpinner() {
        //Creating a spinner value factory with the minimum and maximum ranges available for star polygon point #
        SpinnerValueFactory<Integer> pointValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_STAR_POLYGON_POINTS, MAX_STAR_POLYGON_POINTS);

        //Providing the default number of star polygon points
        pointValueFactory.setValue(DEFAULT_STAR_POLYGON_POINTS);

        starPolygonPointSpinner.setValueFactory(pointValueFactory);

        //Adding a listener to the spinner to update the star polygon tool's number of points
        starPolygonPointSpinner.valueProperty().addListener((_, _, _) -> {
            //Finding the starr polygon tool and setting its value to the new point value
            for(PaintTool paintTool : paintToolbox.getShapeTools()) {
                if(paintTool instanceof StarPolygonTool) {
                    ((StarPolygonTool) paintTool).setNumberOfPoints(starPolygonPointSpinner.getValue());
                }
            }
        });

        //Making sure an initial size is set for the tool
        for(PaintTool paintTool : paintToolbox.getShapeTools()) {
            if(paintTool instanceof StarPolygonTool) {
                ((StarPolygonTool) paintTool).setNumberOfPoints(starPolygonPointSpinner.getValue());
            }
        }
    }

    /**
     * Updates the active tool's color when the colorPicker chooses a different color.
     */
    public void setColor() {
        paintToolbox.getActiveTool().setToolColor(colorPicker.getValue());

        PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),
                "Tool Color Changed to " + colorPicker.getValue());
    }

    /**
     * Runs when the user clicks the toggle for dashing lines and shapes. Calls the PaintToolbox for this operation.
     */
    public void dashingCheckToggled() {
        //If the box is checked
        //Runs if the box is not checked
        paintToolbox.getActiveTool().setLineDashing(dashingCheckBox.isSelected());

        PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),"Line Dashing CheckBox Toggled");

    }

    /**
     * Runs the secret Easter egg. :)
     */
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

            PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),
                    "Someone wanted to learn about Java");

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

    /**
     * Sets up autosave functionally. Specifically, the timer's start and stop behavior.
     */
    private void autosaveSetup() {
        //Setting up Autosave by creating a AutosaveTimer Object
        autosaveTimer = new AutosaveTimer(autosaveTimerLabel, webServerCheckBox, canvasTabManager, fileManager);

        //When a different tab is selected check to see if autosave needs to stop (because the tab has never been saved)
        tabPane.getSelectionModel().selectedItemProperty().addListener((_, _, newTab) -> {
            // This code runs whenever the selected tab changes
            if (newTab instanceof CanvasTab) {
                if(((CanvasTab) newTab).hasNewCanvas()) {
                    autosaveTimer.stopTimer();
                } else {
                    autosaveTimer.startTimer();
                }
            }
        });
    }

    /**
     * This method runs whenever the user selects the "Autosave Notifications" Check Box in the menu bar. It updates
     * the status of the autosave timers flag for autosave notifications.
     */
    public void autosaveNotificationToggle() {
        autosaveTimer.setNotificationsRequest(autosaveNotificationCheckBox.isSelected());

        PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),"Autosave Notifications Toggled");
    }

    /**
     * Called when the user closes a tab from the file menu. Calls the DialogManager to do so.
     */
    public void closeTab() {
        DialogManager.closeTab(canvasTabManager, fileManager, webServer);

        PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),"Tab Closed");
    }

    /**
     * Runs when the user pushes the clear canvas button. The clearing itself occurs within the active CanvasTab.
     */
    public void clearCanvas() {
        canvasTabManager.getActiveTab().clearCanvas();

        PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),"Canvas Clearing Attempted");
    }

    /**
     * Quits Pain(t). Runs when the user pushes the "Quit Pain(t)" button in the file menu.
     */
    public void quitPaint() {
        //Simulating the closing of the window by firing an event
        //Will allow the smartSave method to still run.
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

}
