package cs250.paint;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.util.Duration;

/**
 * The AutosaveTimer class controls the file autosave timer for pain(t).
 * This includes managing a label to remaining time. Additionally, the timing system in this class also manipulates
 * the application's web server checkbox because the webserver only needs to display an image when the autosave timer
 * is running.
 */
public class AutosaveTimer {
    //These time values are in seconds
    private static final int INITIAL_TIME = 300;
    //private static final int INITIAL_TIME = 300;
    private int timeRemaining;

    //Each AutosaveTimer has a label
    private final Label timerLabel;

    //The checkbox that also needs to be controlled by this class
    private final CheckMenuItem webServerCheckBox;

    //The timeline that will handle running an event (counting a timer) every second
    private final Timeline timeline;

    //The canvas tab manager is used by the web server check box
    private final CanvasTabManager canvasTabManager;


    /**
     * Constructs an AutosaveTimer with the passed in label for displaying time and calls the startTimer method.
     * @param timerLabel
     * A label remaining time for the timer will be displayed on.
     * @param webServerCheckBox
     * The CheckMenuItem for the PaintWebServer that the autosave timer will help manage. This includes things such as
     * disabling the checkbox when the autosave timer is off.
     */
    public AutosaveTimer(Label timerLabel, CheckMenuItem webServerCheckBox, CanvasTabManager canvasTabManager,
                         FileManager fileManager) {
        this.timerLabel = timerLabel;
        this.webServerCheckBox = webServerCheckBox;
        this.canvasTabManager = canvasTabManager;

        timeRemaining = INITIAL_TIME;

        //Initializing a timeline object that will do something each second
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), _ -> {
            if (timeRemaining > 0) {
                //There is time left on the timer
                timeRemaining--;
                timerLabel.setText(formatTime(timeRemaining));
            } else {
                // Timer is finished, reset and restart
                timeRemaining = INITIAL_TIME;
                timerLabel.setText(formatTime(timeRemaining));

                //Saving all canvas' only once the JavaFX UI is ready.
                //Should reduce slowdown
                //This method would be called on a separate thread but because it uses JavaFX components, that is not
                //possible. (JavaFX is single threaded)
                Platform.runLater(() -> {
                   for (Tab tab : canvasTabManager.getTabPane().getTabs()) {
                       //Only saving if our tab has unsaved changes, is not a blank canvas, and is actually a canvas tab
                       if(tab instanceof CanvasTab && ((CanvasTab) tab).hasUnsavedChanges() &&
                               !((CanvasTab) tab).hasNewCanvas()) {
                           fileManager.saveCanvas((CanvasTab) tab);
                       }
                   }
                });
            }
        }));
        //Have  the timeline run forever until it is manually stopped
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Starts the autosave timer and updates the GUI web server check box accordingly.
     */
    public void startTimer() {
        timeline.play();

        webServerCheckBox.setSelected(canvasTabManager.getActiveTab().isRequestedForServer());
        webServerCheckBox.setDisable(false);
    }

    /**
     * Stops the autosave timer and updates the GUI web server check box accordingly.
     */
    public void stopTimer() {
        timeline.stop();
        timerLabel.setText("-:--");

        webServerCheckBox.setSelected(false);
        webServerCheckBox.setDisable(true);
    }

    /**
     * Formats a time duration in seconds to minutes:seconds.
     * @param seconds
     * An integer value of seconds
     * @return
     * Formatted time as a String
     */
    public static String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

}
