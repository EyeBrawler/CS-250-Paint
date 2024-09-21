package cs250.paint;

import cs250.paint.PaintTools.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.util.Optional;


public class SceneController {

    //Reference to stage
    private Stage stage;

    //The main anchor pane that all components stem from
    //Injected so that the stage can be retrieved
    @FXML AnchorPane mainPane;

    //The Tab Pane in where tabs for different open canvas' are
    @FXML TabPane tabPane;

    //This value will come from the CanvasTabManager Class
    private CanvasTabManager canvasTabManager;

    //Class variable to store a fileManager object
    //The file manager class has methods for completing file operations such as loading and saving files
    private FileManager fileManager;

    //Menu Bar Nodes
    //Controls the number of sides used by the polygon tool
    @FXML private Spinner<Integer> polygonSideSpinner;
    private static final int MAX_POLYGON_SIDES = 9999 ;
    private static final int MIN_POLYGON_SIDES = 3;
    private static final int DEFAULT_POLYGON_SIDES = 3;

    //Toolbar Items
    //These objects control many of the paint tool attributes
    @FXML private ChoiceBox<PaintTool> shapeToolChoice;
    @FXML private Spinner<Integer> toolWidthSpinner;
    @FXML private ColorPicker colorPicker;
    @FXML private CheckBox dashingCheckBox;
    //Toggle Group for toolbar buttons
    //Enables the selection of one button to disable the selection of others
    //The image tool and drawing tool buttons fall into this group
    @FXML private ToggleGroup toolbarToggleGroup;

    //The toolbar buttons themselves
    //They are being injected into this class from the FXML so that tool objects can be added to their user data
    @FXML private ToggleButton selectAreaButton;
    @FXML private ToggleButton textInsertButton;
    @FXML private ToggleButton lineButton;
    @FXML private ToggleButton pencilButton;
    @FXML private ToggleButton eraserButton;

    //Check box to toggle if selection tool is in cut or copy mode
    @FXML private CheckMenuItem cutModeCheckBox;


    //Variables to set tool width limits
    private static final int MIN_TOOL_WIDTH = 1;
    private static final int MAX_TOOL_WIDTH = 1024;

    private static final int DEFAULT_TOOL_WIDTH = 10;

    //The paintToolbox to manage paint tools
    PaintToolbox paintToolbox;


    //Function ran by JavaFX after the scene is loaded
    //This contains a significant amount of miscellaneous setup code.
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

        //Setting up the shape Tool Choice ChoiceBox for tool selection
        shapeToolChoice.getItems().addAll(paintToolbox.getShapeTools());

        //To have an item selected by default for the shape tools, the first item in tool list is selected
        //with the setValue method
        shapeToolChoice.setValue(paintToolbox.getShapeTools().getFirst());
        //Using the :: method reference operator to link method to ChoiceBox
        shapeToolChoice.setOnAction(this::setShapeTool);


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

    public void postInitialization() {
        //Running smartSaveSetup() method
        //It allows a confirmation dialog box when the user has modified the canvas but not yet saved.
        //It is occurring here because this is the next code that runs after the initialize method.
        smartSaveSetup();

        //Because the file manager needs access to the stage and the stage is not available until postInitialization,
        //The fileManager is initialized here
        fileManager = new FileManager(canvasTabManager, stage);
    }

    //Setter method to inject the Stage into this class
    //Called in main class
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    //Method for the about popup that can be found in the menu bar
    public void aboutMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Pain(t)");
        alert.setContentText("Version 0.4\nPain(t) is a less professional recreation of MS Paint" +
                "\nCreated By Sam Thyen");

        alert.showAndWait();
    }


    //Method for opening an image file.
    public void openDialog() {
        //Warning the user that opening an image in the current tab will overwrite unsaved changes
        if(canvasTabManager.getActiveTab().hasUnsavedChanges()) {
            //Constructing a confirmation alert
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Are you sure?");
            alert.setHeaderText("Opening an image in a tab will overwrite your unsaved changes.");
            alert.setContentText("Do you want to open an image in this tab?");

            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

            Optional<ButtonType> result = alert.showAndWait();

            //result should always be present
            //If not, something is very wrong
            if (result.isPresent()) {

                //Dialog Box Outcomes
                if (result.get() == ButtonType.YES) {
                    //user chooses yes
                    fileManager.openImage(canvasTabManager.getActiveTab());

                } //If user chooses no, we do nothing.
            }
        } else {
            fileManager.openImage(canvasTabManager.getActiveTab());
        }
    }

    public void openNewTabDialog() {
        //Making a new tab
        canvasTabManager.newTab();

        //Opening an image in that new tab
        fileManager.openImage(canvasTabManager.getLastCanvasTab());

        //Switching to the tab where the image has been opened
        //The -2 exists because the size is always 1 larger than the max index and the last tab is the add tab button
        tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);
    }

    public void saveAsDialog() {
        fileManager.saveAsDialog();
    }


    public void saveCanvas() {
        fileManager.saveCanvas();
    }

    public void undo() {
        canvasTabManager.getActiveTab().undo();
    }
    public void redo() {
        canvasTabManager.getActiveTab().redo();
    }

    public void cutSelectionToggle() {
        if(paintToolbox.getActiveTool() instanceof SelectTool) {
            ((SelectTool) paintToolbox.getActiveTool()).setCutMode(cutModeCheckBox.isSelected());
        }
    }

    //This function creates a custom dialog box where the user can resize the canvas.
    //See canvas tab manager for more details
    public void resizeCanvas() {
        canvasTabManager.getActiveTab().resizeCanvas();
    }

    //Method to change the active shape tool based on the shapeToolChoice ChoiceBox
    public void setShapeTool(ActionEvent event) {
        paintToolbox.setActiveTool(shapeToolChoice.getValue(), canvasTabManager.getActiveTab().getGraphicsContext(),
                colorPicker.getValue(), toolWidthSpinner.getValue(), dashingCheckBox.isSelected());

        toolbarToggleGroup.selectToggle(null);
    }

    //Method for setting the active toolbar tool.
    //It also deselects any tool that is currently selected within the shape tools
    public void setToolbarTool() {
        //First checking if the there is no toggle buttons selected, in this case the tool choice will default back to
        //the selected shape
        if (toolbarToggleGroup.getSelectedToggle() == null) {
            paintToolbox.setActiveTool(shapeToolChoice.getValue(), canvasTabManager.getActiveTab().getGraphicsContext(),
                    colorPicker.getValue(), toolWidthSpinner.getValue(), dashingCheckBox.isSelected());
        } else {
            //Passing the tool associated with the toggle button
            //Casting the userDataObjet as a paint tool because each toggle button has a paint tool associated with it
            try {
                paintToolbox.setActiveTool((PaintTool)toolbarToggleGroup.getSelectedToggle().getUserData(),
                        canvasTabManager.getActiveTab().getGraphicsContext(), colorPicker.getValue(),
                        toolWidthSpinner.getValue(), dashingCheckBox.isSelected());
            } catch(Exception e) {
                System.out.println("Error: A type mismatch occurred when trying to set the active tool.");

            }
        }

    }

    //Setting up the spinner for tool width
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

    //This method sets up the spinner found in the shape section of the menu bar.
    //The spinner is for controlling the number of sides the polygon tool's polygon will have
    public void setupPolygonSideSpinner() {
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

    //Method for updating the active tool's color when the colorPicker chooses a different color
    public void setColor() {
        paintToolbox.getActiveTool().setToolColor(colorPicker.getValue());
    }

    public void dashingCheckToggled() {
        //If the box is checked
        //Runs if the box is not checked
        paintToolbox.getActiveTool().setLineDashing(dashingCheckBox.isSelected());

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
            if (canvasTabManager.hasUnsavedChanges()) {
                event.consume();  // Stop the default close behavior

                //If the tab pane has only two tabs (one open tab and the new tab button (which is technically a tab)
                //Then we will ask the user if they want to save the file
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                if(tabPane.getTabs().size() <= 2) {
                    //Constructing a confirmation alert
                    alert.setTitle("Save Changes?");
                    alert.setHeaderText("You have attempted to exit Pain(t) without saving.");
                    alert.setContentText("Would you like to save the tab before closing?");


                    alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

                    Optional<ButtonType> result = alert.showAndWait();

                    //result should always be present
                    //If not, something is very wrong
                    if (result.isPresent()) {

                        //Dialog Box Outcomes
                        if (result.get() == ButtonType.YES) {
                            //user chooses yes
                            fileManager.saveCanvas();
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
                    //In this case the user has multiple tabs open and we are just going to ask them if they are sure
                    //they want to exit instead.
                    //Constructing a confirmation alert
                    alert.setTitle("Unsaved Changes");
                    alert.setHeaderText("You have attempted to exit Pain(t) with multiple unsaved tabs.");
                    alert.setContentText("Are you sure you would like to exit?");

                    alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

                    Optional<ButtonType> result = alert.showAndWait();

                    //result should always be present
                    //If not, something is very wrong
                    if (result.isPresent()) {

                        //Dialog Box Outcomes
                        if (result.get() == ButtonType.YES) {
                            //user chooses yes
                            //Close Pain(t)
                            stage.close();
                            Platform.exit();
                            System.exit(0);

                        } else {
                            //If they said no we just consume the event and Pain(t) keeps running
                            event.consume();
                        }
                    }
                }
            } else {
                //What we do if the user has saved everything. (close Pain(t))
                stage.close();
            }

        });

    }

    //This method is called when the user closes a tab from the file menu
    public void closeTab() {
        if (canvasTabManager.getActiveTab().hasUnsavedChanges()) {
            //Constructing a confirmation alert
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Save Changes?");
            alert.setHeaderText("You have attempted to close a tab without saving its changes.");
            alert.setContentText("Would you like to save the selected tab before closing it?");


            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

            Optional<ButtonType> result = alert.showAndWait();

            //result should always be present
            //If not, something is very wrong
            if (result.isPresent()) {

                //Dialog Box Outcomes
                if (result.get() == ButtonType.YES) {
                    //user chooses Yes
                    fileManager.saveCanvas();
                    canvasTabManager.closeSelectedTab();

                } else if (result.get() == ButtonType.NO) {
                    //User chooses no
                    //Just close the tab
                    canvasTabManager.closeSelectedTab();

                }
                //When the user chooses cancel, nothing happens
            }

        } else {
            //What we do if the user has saved. (close the tab only)
            canvasTabManager.closeSelectedTab();
        }
    }

    public void clearCanvas() {
        canvasTabManager.getActiveTab().clearCanvas();
    }


    public void quitPaint() {
        //Simulating the closing of the window by firing an event
        //Will allow the smartSave method to still run.
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

}
