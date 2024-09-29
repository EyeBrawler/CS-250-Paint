package cs250.paint;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * The File Manager class contains methods for file IO operations.
 */
public class FileManager {
    private final Stage stage;
    private AutosaveTimer autosaveTimer;

    /**
     * A basic constructor that passes in a JavaFX Stage so that the FileManager can know from where to open file
     * choosing dialogs.
     * @param stage
     * A JavaFX stage object that will be used to display file choosing dialogs.
     */
    public FileManager(Stage stage) {
        this.stage = stage;
    }

    /**
     * Passes an AutosaveTimer object into the class.
     * @param autosaveTimer
     * This Autosave timer will be updated (started, stopped) during file operations.
     */
    public void setAutosaveTimer(AutosaveTimer autosaveTimer) {
        this.autosaveTimer = autosaveTimer;
    }

    /**
     * Opens an image on the currently selected canvas based upon user's selection.
     * @param tabToOpenImage
     * The currently active tab.
     */
    public void openImage(CanvasTab tabToOpenImage) {
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
        tabToOpenImage.setOpenFile(fileChooser.showOpenDialog(stage));

        //Try Catch is to handle any errors that occur in opening the image.
        if(tabToOpenImage.getOpenFile() != null) {
            try {
                //Creating FileInputStream from the file.
                FileInputStream inputStream = new FileInputStream(tabToOpenImage.getOpenFile());
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
            tabToOpenImage.getCanvas().setHeight(openedImage.getHeight());
            tabToOpenImage.getCanvas().setWidth(openedImage.getWidth());

            //Drawing Image to graphics context
            tabToOpenImage.getGraphicsContext().drawImage(openedImage, 0, 0);

            //Giving the tab the name of the file
            tabToOpenImage.setText(tabToOpenImage.getOpenFile().getName());

            //Because a new image has been opened, any unsaved changes are now saved because the open file is saved.
            tabToOpenImage.setUnsavedChanges(false);

            //Restarting the autosave timer in case it has been stopped from the tab being new
            autosaveTimer.startTimer();

            //Updating the canvas' file extension
            tabToOpenImage.setFileType(getFileExtension(tabToOpenImage.getOpenFile().getName()));

            //Updating the buffered image associated with the tab. Allows the web server to get the image on opening.
            updateBufferedImage(tabToOpenImage);
        }
    }

    /**
     * Displays a SaveAs dialog. This method also contains a warning for file conversions and restarts the autosave
     * timer if a new file has been saved.
     * @param activeTab
     * The tab the SaveAs dialog will act upon.
     */
    public void saveAsDialog(CanvasTab activeTab) {
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
        activeTab.setOpenFile(fileChooser.showSaveDialog(stage));

        //Calling the saveCanvas() method if a file for saving has been selected.
        if(activeTab.getOpenFile() != null) {

            //Checking to see if we are converting file formats
            if((activeTab.getFileType() != null) && !(activeTab.getFileType().toLowerCase().contentEquals
                    (Objects.requireNonNull(getFileExtension(activeTab.getOpenFile().getName().toLowerCase()))))) {
                //When we are, display an alert
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("File Conversion Warning");
                alert.setContentText("Converting file formats can result in image data loss.");

                alert.showAndWait();

            }

            saveCanvas(activeTab);

            //Renaming the tab to the file's name
            activeTab.setText(activeTab.getOpenFile().getName());

            //Restarting the autosave timer in case it has been stopped from the tab being new
            autosaveTimer.startTimer();
        }


    }

    /**
     * Saves the canvas to a currently loaded file. If there is no loaded file this method will call saveAsDialog()
     * @param activeTab
     * The tab that will have it's canvas saved to file.
     */
    public void saveCanvas(CanvasTab activeTab) {
        //Forcing Save As Dialog when there is no openFile to write over
        //Allows the user to pick a file extension
        //Also prevents not knowing image type for saving.

        if(activeTab.getOpenFile() == null) {
            saveAsDialog(activeTab);

        } else {
            String fileName = activeTab.getOpenFile().getName();
            activeTab.setFileType(getFileExtension(fileName));

            updateBufferedImage(activeTab);

            //Checking if the fileExtension exists and then writing the file using the buffered image
            if(activeTab.getFileType() != null) {
                try {
                    // Write the image to the file using the appropriate format
                    ImageIO.write(activeTab.getBufferedImage(), activeTab.getFileType(), activeTab.getOpenFile());
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

    /**
     * Takes a snapshot of the active tab's canvas and updates that tab's buffered image object. This method does
     * account for file conversion.
     * @param activeTab
     * The tab that will have its buffered image updated.
     */
    public void updateBufferedImage(CanvasTab activeTab) {
        //WritableImage is the image format the screenshot function uses
        //Taking a screenshot of the canvas is how the image will be saved
        WritableImage writableImage =
                activeTab.getCanvas().snapshot(null,null);

        //Buffered Images have a type, this type will determine if imageIO can work with it.
        //ARGB is for PNG
        //RGB is for JPG and BMP
        //It is likely created originally with ARGB
        activeTab.setBufferedImage(SwingFXUtils.fromFXImage(writableImage, null));

        //Convert to correct type by redrawing if not png
        if(!"png".equals(activeTab.getFileType())) {
            //Creating the temporary buffered image needed for redrawing
            BufferedImage tempBufferedImage = new BufferedImage(activeTab.getBufferedImage().getWidth(),
                    activeTab.getBufferedImage().getHeight(), BufferedImage.TYPE_INT_RGB);

            //Redrawing with Graphics2D
            Graphics2D tempGraphics = tempBufferedImage.createGraphics();
            tempGraphics.drawImage(activeTab.getBufferedImage(), 0, 0, null);
            tempGraphics.dispose();

            activeTab.setBufferedImage(tempBufferedImage);
        }
    }

    /**
     * Static Method for getting a file name extension from a file name string
     * The only reason this method is public is for unit testing
     * @param fileName
     * A string containing the name of a file.
     * @return
     * A string of the input file's extension.
     */
    public static String getFileExtension(String fileName) {
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
}
