package cs250.paint;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class CanvasTabManager {
    private final TabPane tabPane;
    private final PaintToolbox paintToolbox;

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

    //Method to add new tab
    //This is a method that can be used by other pieces of code
    public void newTab() {
        //Open a new tab for a blank canvas
        tabPane.getTabs().add(tabPane.getTabs().size() - 1, new CanvasTab(paintToolbox));
    }

    public CanvasTab getActiveTab() {
        //Casting as a canvas tab so that CanvasTab methods can be called on the object
        //Such as the hasUnsavedChanges() method
        return (CanvasTab)tabPane.getSelectionModel().getSelectedItem();
    }

    public CanvasTab getLastCanvasTab() {
        //Checking to make sure second to last tab is really a canvas tab
        if(tabPane.getTabs().get(tabPane.getTabs().size() - 2) instanceof CanvasTab) {
            //Getting the second to last tab (because the add button is a tab) and returning it
            return (CanvasTab) tabPane.getTabs().get(tabPane.getTabs().size() - 2);
        } else {
            return null;
        }
    }

    public void closeSelectedTab() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            tabPane.getTabs().remove(selectedTab);
        }
    }

    //Method to check all tabs to see if they have any unsaved changes
    //If any one tab has unsaved changes, this variable is false
    public boolean hasUnsavedChanges() {
        boolean unsavedChanges = false;

        for(Tab tab : tabPane.getTabs()) {
            //We need to check if our tab is actually a CanvasTab first
            if (tab instanceof CanvasTab canvasTab) {
                if(canvasTab.hasUnsavedChanges()) {
                    unsavedChanges = true;
                    break;
                }
            }
        }

        return unsavedChanges;
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
