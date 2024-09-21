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

public class FileManager {
    private final CanvasTabManager canvasTabManager;
    private final Stage stage;

    public FileManager(CanvasTabManager canvasTabManager, Stage stage) {
        this.canvasTabManager = canvasTabManager;
        this.stage = stage;
    }

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
        canvasTabManager.getActiveTab().setOpenFile(fileChooser.showSaveDialog(stage));

        //Calling the saveCanvas() method if a file for saving has been selected.
        if(canvasTabManager.getActiveTab().getOpenFile() != null) {
            saveCanvas();

            //Renaming the tab to the file's name
            canvasTabManager.getActiveTab().setText(canvasTabManager.getActiveTab().getOpenFile().getName());
        }


    }

    public void saveCanvas() {
        //Forcing Save As Dialog when there is no openFile to write over
        //Allows the user to pick a file extension
        //Also prevents not knowing image type for saving.

        if(canvasTabManager.getActiveTab().getOpenFile() == null) {
            saveAsDialog();

        } else {
            String fileName = canvasTabManager.getActiveTab().getOpenFile().getName();
            String fileExtension = getFileExtension(fileName);

            //WritableImage is the image format the screenshot function uses
            //Taking a screenshot of the canvas is how the image will be saved
            WritableImage writableImage =
                    canvasTabManager.getActiveTab().getCanvas().snapshot(null,null);

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
                    ImageIO.write(bufferedImage, fileExtension, canvasTabManager.getActiveTab().getOpenFile());
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
}
