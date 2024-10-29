package cs250.paint;

import cs250.paint.PaintLogger.PaintLogger;
import cs250.paint.WebServer.PaintWebServer;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * This class manages dialog boxes and calls methods for opening files, saving and closing tabs, and smart save (when
 * appropriate).
 */
public class DialogManager {

    /**
     * Sets up smart saving. This is a warning system that will alert the user via a dialog box and options when there
     * are unsaved changes within Pain(t).
     * @param stage
     * The stage where smart saving behavior will occur.
     * @param canvasTabManager
     * The CanvasTabManager that will be searched for unsaved changes.
     * @param fileManager
     * An operational Pain(t) FileManager for handling file operations.
     */
    public static void smartSaveSetup(Stage stage, CanvasTabManager canvasTabManager, FileManager fileManager) {
        //Setting the stage's new close behavior for smart saving
        stage.setOnCloseRequest(event -> {
            if (canvasTabManager.hasUnsavedChanges()) {
                event.consume();  // Stop the default close behavior

                //If the tab pane has only two tabs (one open tab and the new tab button (which is technically a tab)
                //Then we will ask the user if they want to save the file
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                if(canvasTabManager.getTabPane().getTabs().size() <= 2) {
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
                            fileManager.saveCanvas(canvasTabManager.getActiveTab());
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
                    //In this case the user has multiple tabs open, and we are just going to ask them if they are sure
                    //they want to exit instead.
                    //Constructing a confirmation alert
                    alert.setTitle("Unsaved Changes");
                    alert.setHeaderText("You have attempted to exit Pain(t) unsaved changes.");
                    alert.setContentText("Would you like to save all open tabs that have associated files?");

                    alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

                    Optional<ButtonType> result = alert.showAndWait();

                    //result should always be present
                    //If not, something is very wrong
                    if (result.isPresent()) {

                        //Dialog Box Outcomes
                        if (result.get() == ButtonType.YES) {
                            //Logging Operation and Shutting Down the logger
                            PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),"Paint Exited");
                            PaintLogger.shutdownLogger();

                            //user chooses yes, save all tabs
                            //then Close Pain(t)
                            saveAllTabs(canvasTabManager, fileManager);
                            stage.close();
                            Platform.exit();
                            System.exit(0);

                        } else if(result.get() == ButtonType.NO) {
                            //Logging Operation and Shutting Down the logger
                            PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),"Paint Exited");
                            PaintLogger.shutdownLogger();

                            //User chooses no
                            //Just Close Pain(t)
                            stage.close();
                            Platform.exit();
                            System.exit(0);
                        } else {
                            //If they said cancel we just consume the event and keep Pain(t) running.
                            event.consume();
                        }
                    }
                }
            } else {
                //Logging Operation and Shutting Down the logger
                PaintLogger.logOperation(canvasTabManager.getActiveTab().getText(),"Paint Exited");
                PaintLogger.shutdownLogger();

                //What we do if the user has saved everything. (close Pain(t))
                stage.close();
                Platform.exit();
                System.exit(0);
            }

        });

    }

    /**
     * Method for closing a tab outside a tab's "x" button.
     * @param canvasTabManager
     * The CanvasTabManager in which the active tab will come from
     * @param fileManager
     * The FileManager that can be used to save changes if the user requests it.
     * @param webServer
     * The PaintWebServer that may need to be notified of the closed tab.
     */
    public static void closeTab(CanvasTabManager canvasTabManager, FileManager fileManager, PaintWebServer webServer) {
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
                    fileManager.saveCanvas(canvasTabManager.getActiveTab());
                    canvasTabManager.closeSelectedTab();

                } else if (result.get() == ButtonType.NO) {
                    //User chooses no
                    //Just close the tab

                    if(canvasTabManager.getActiveTab().getOpenFile() != null) {
                        //Make sure the web server cannot display the image anymore by removing the context.
                        webServer.removeImagePage(canvasTabManager.getActiveTab().getOpenFile().getName());
                    }


                    canvasTabManager.closeSelectedTab();

                }
                //When the user chooses cancel, nothing happens
            }

        } else {
            //What we do if the user has saved. (close the tab only)
            canvasTabManager.closeSelectedTab();
        }
    }

    /**
     * A method for saving all tabs that will display a message when unsaved "new canvas" tabs are open.
     * @param canvasTabManager
     * The CanvasTabManager that will be searched for unsaved tabs.
     * @param fileManager
     * The FileManager used for saving tabs that are savable.
     */
    public static void saveAllTabs(CanvasTabManager canvasTabManager, FileManager fileManager) {
        if(canvasTabManager.hasNewCanvas()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Unsaved New Canvas Tabs Open");
            alert.setContentText("Your open new canvas tabs will not be saved with this operation.");

            alert.showAndWait();
        }
        for (Tab tab : canvasTabManager.getTabPane().getTabs()) {
            //Only saving if our tab has unsaved changes, is not a blank canvas, and is actually a canvas tab
            if(tab instanceof CanvasTab && !((CanvasTab) tab).hasNewCanvas()) {
                fileManager.saveCanvas((CanvasTab) tab);
            }
        }
    }

    /**
     * Opens a file through the FileManager class. If an open tab is going to be overwritten, the user is asked if they
     * are sure they would still like to open the new image.
     * @param canvasTabManager
     * The CanvasTabManager that will be used for finding the active tab.
     * @param fileManager
     * The FileManager that will be called for opening a new file.
     */
    public static void openFile(CanvasTabManager canvasTabManager, FileManager fileManager) {
        //Warning the user that opening an image in the current tab will overwrite unsaved changes
        if (canvasTabManager.getActiveTab().hasUnsavedChanges()) {
            //Constructing a confirmation alert
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Are you sure?");
            alert.setHeaderText("Opening an image in this tab will overwrite your unsaved changes.");
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
}
