module cs250.paint {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;
    requires javafx.web;
    requires jdk.httpserver;


    opens cs250.paint to javafx.fxml;
    exports cs250.paint;
    exports cs250.paint.PaintTools;
    opens cs250.paint.PaintTools to javafx.fxml;
    exports cs250.paint.WebServer;
    opens cs250.paint.WebServer to javafx.fxml;
}