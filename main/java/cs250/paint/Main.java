package cs250.paint;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

//Pain(t) by Sam Thyen
/**
 * The Main class which creates the main window and loads the primary scene FXML file.
 */
public class Main extends Application {
    /**
     * The main function which calls the JavaFX launch function.
     * @param args
     * Command line arguments for Pain(t) (not used).
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     *Loads the primary FXML file, creates and shows the stage;
     * @param stage
     * The main stage
     * @throws IOException
     * If the FXML file cannot be loaded, an IOException is thrown. This could happen
     * due to a missing or corrupted FXML file, or if there is an issue with reading
     * the file from the resource path.
     */
    @Override
    public void start(Stage stage) throws IOException {
        //Loading FXML File
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("PrimaryScene.fxml"));
        //Loading the scene to a Scene called scene
        Scene scene = new Scene(fxmlLoader.load());

        // Get the controller instance from the FXMLLoader
        SceneController controller = fxmlLoader.getController();
        // Pass the Stage to the controller
        controller.setStage(stage);
        controller.postInitialization();

        //Setting window title and populating program icon from resources folder
        stage.setTitle("Pain(t)");
        Image icon = new Image(String.valueOf(Main.class.getResource("icons/PaintIcon.png")));
        stage.getIcons().add(icon);

        //Adding the scene to the stage (window)
        stage.setScene(scene);
        stage.show();
    }

}