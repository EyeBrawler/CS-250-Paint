module cs250.paint {
    requires javafx.fxml;
    requires javafx.swing;
    requires javafx.web;
    requires jdk.httpserver;
    requires org.controlsfx.controls;
    requires java.logging;


    opens cs250.paint to javafx.fxml;
    exports cs250.paint;
    exports cs250.paint.PaintTools;
    opens cs250.paint.PaintTools to javafx.fxml;
    exports cs250.paint.WebServer;
    opens cs250.paint.WebServer to javafx.fxml;
    exports cs250.paint.PaintLogger;
    opens cs250.paint.PaintLogger to javafx.fxml;
}