package cs250.paint;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * The special scene controller for the "What is Java?" Easter egg.
 */
public class SpecialSceneController {
    @FXML private WebView webView;
    private Stage stage;
    private WebEngine engine;

    /**
     * An initialize method for this scene. Sets up the Easter Egg.
     */
    @FXML
    public void initialize() {
        //Creating a web engine from the web view to load the special webpage
        engine = webView.getEngine();
        engine.load("https://www.youtube.com/watch?v=fqwIpH6phJs");
    }

    /**
     * Method to bring in a newly created stage from the SceneController class and give it proper closing behavior. This
     * is ultimately so the YouTube window cannot run in the background.
     * @param stage
     * The stage to be brought in from the SceneController.
     */
    public void setStage(Stage stage) {
        this.stage = stage;

        //Loading a blank webpage once the window is closed so the video stops playing
        //This is done as part of setting the stage so that the stage is not seen as null but also
        // has access to the web engine.
        stage.setOnCloseRequest(_ -> engine.load("about:blank"));
    }

}
