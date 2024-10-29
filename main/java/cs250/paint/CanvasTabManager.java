package cs250.paint;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Class for managing an infinite number of CanvasTab objects and controlling the tab pane's behavior. The gateway for
 * the rest of Pain(t) not within a CanvasTab to access one.
 */
public class CanvasTabManager {
    private final TabPane tabPane;
    private final PaintToolbox paintToolbox;

    /**
     * Setting up the canvas tab manager with a blank tab and add tab button (which is really just a tab).
     * @param tabPane
     * The TabPane to be managed by the CanvasTabManager
     * @param paintToolbox
     * The paintToolbox the canvasTabManager will pass to its CanvasTabs.
     */
    public CanvasTabManager(TabPane tabPane, PaintToolbox paintToolbox) {
        this.tabPane = tabPane;
        this.paintToolbox = paintToolbox;

        //Making the first tab which is a blank canvas
        CanvasTab newCanvasTab = new CanvasTab(paintToolbox);
        tabPane.getTabs().add(newCanvasTab);

        tabPane.getTabs().add(newTabButton());

        tabPane.getSelectionModel().selectedItemProperty().addListener((_, _, newTab) -> {
            // This code runs whenever the selected tab changes
            if (newTab instanceof CanvasTab) {
                paintToolbox.getActiveTool().setGraphicsContext(((CanvasTab) newTab).getGraphicsContext());
            }
        });

    }

    /**
     * Adds a new tab to the tabPane.
     */
    public void newTab() {
        //Open a new tab for a blank canvas
        tabPane.getTabs().add(tabPane.getTabs().size() - 1, new CanvasTab(paintToolbox));
    }

    /**
     * Acquires the currently active (or selected) Pain(t) tab.
     * @return
     * The currently active CanvasTab object.
     */
    public CanvasTab getActiveTab() {
        //Casting as a canvas tab so that CanvasTab methods can be called on the object
        //Such as the hasUnsavedChanges() method
        return (CanvasTab)tabPane.getSelectionModel().getSelectedItem();
    }

    /**
     * Returns the last tab in the tab pane.
     * @return
     * The CanvasTab object for the very end (far right side) of the tab pane.
     */
    public CanvasTab getLastCanvasTab() {
        //Checking to make sure second to last tab is really a canvas tab
        if(tabPane.getTabs().get(tabPane.getTabs().size() - 2) instanceof CanvasTab) {
            //Getting the second to last tab (because the add button is a tab) and returning it
            return (CanvasTab) tabPane.getTabs().get(tabPane.getTabs().size() - 2);
        } else {
            return null;
        }
    }

    /**
     * Returns the TabPane object all canvas tabs are stored in.
     * @return
     * The tab pane for canvas tabs.
     */
    public TabPane getTabPane() {
        return tabPane;
    }

    /**
     * Removes the currently selected/active tab from the tab pane
     */
    public void closeSelectedTab() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            tabPane.getTabs().remove(selectedTab);
        }
    }

    //
    //

    /**
     * Method to check all tabs to see if they have any unsaved changes.
     * @return
     * If any one tab has unsaved changes, false is returned.
     */
    public boolean hasUnsavedChanges() {
        for(Tab tab : tabPane.getTabs()) {
            //We need to check if our tab is actually a CanvasTab first
            if (tab instanceof CanvasTab canvasTab) {
                if(canvasTab.hasUnsavedChanges()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks to see if any canvas tabs in the tab pane are new canvases (not associated with a file).
     * @return
     * Boolean true if any tabs are not associated with a file.
     */
    public boolean hasNewCanvas() {
        for(Tab tab : tabPane.getTabs()) {
            //We need to check if our tab is actually a CanvasTab first
            if (tab instanceof CanvasTab canvasTab) {
                if(canvasTab.hasNewCanvas()) {
                    return true;
                }
            }
        }

        return false;
    }

    //Code inspired by Noah on Stack Overflow
    //https://stackoverflow.com/questions/62129461/how-to-create-a-add-tab-button-in-javafx
    // Tab that acts as a button and adds a new tab and selects it
    private Tab newTabButton() {
        Tab addTab = new Tab("+"); // You can replace the text with an icon

        addTab.setClosable(false);

        //The observable, old tab, and new tab are all parameters for the tab selection action listener
        //newTab is the tab that the user selects
        //The condition below checks to see if the tab just switched to is the addTab
        tabPane.getSelectionModel().selectedItemProperty().addListener((_, _, newTab) -> {
            if(newTab == addTab) {
                // Adding new tab before the "button" tab
                tabPane.getTabs().add(tabPane.getTabs().size() - 1, new CanvasTab(paintToolbox));

                // Selecting the tab before the button, which is the newly created one
                tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);

            }
        });
        return addTab;
    }
}
