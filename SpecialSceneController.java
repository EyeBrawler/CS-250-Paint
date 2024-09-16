package cs250.paint;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class SpecialSceneController {
    @FXML private WebView webView;
    Stage stage;
    WebEngine engine;

    @FXML
    public void initialize() {
        //Creating a web engine from the webview to load the special webpage
        engine = webView.getEngine();
        engine.load("https://www.youtube.com/watch?v=fqwIpH6phJs");

    }

    //Method to bring in the stage from the SceneController class
    public void setStage(Stage stage) {
        this.stage = stage;

        //Loading a blank webpage once the window is closed so the video stops playing
        //This is done as part of setting the stage so that the stage is not seen as null but also
        // has access to the web engine.
        stage.setOnCloseRequest(event -> {
            engine.load("about:blank");
        });
    }

}
