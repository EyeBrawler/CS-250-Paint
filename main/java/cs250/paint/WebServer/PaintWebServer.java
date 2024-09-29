package cs250.paint.WebServer;

import com.sun.net.httpserver.HttpServer;
import cs250.paint.CanvasTab;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * The PaintWebServer class is used for creating a web server designed to display canvas' upon the autosave and saving of
 * files requested by the user for being sent to the server.
 */
public class PaintWebServer {
    private HttpServer server;

    /**
     * Initializes an HttpServer class and displays messages once the server is running.
     */
    public PaintWebServer() {
        try {

            //Creating the Http server
            server = HttpServer.create(new InetSocketAddress(8080), 0);

            //The executor would manage how threads are handled for each server request, the default (which means the
            //executor is null) just creates 1 new thread for each page request.
            server.setExecutor(null);

            server.start();

            System.out.println("Server is running on port 8080...\nat localhost:8080/\"name of requested tab\"");
        } catch (IOException e) {
            System.out.println("Error starting the web server: " + e.getMessage());
        }

    }

    /**
     * Creates a new context (page) for the web server
     * @param pageName
     * The name of the new URI
     * @param canvasTab
     * The Canvas tab that will be referred to when creating the web page.
     */
    public void addImagePage(String pageName, CanvasTab canvasTab) {
        server.createContext("/" + pageName, new ImageHandler(canvasTab));
    }

    /**
     * Removes a context from the web server
     * @param pageName
     * The name of the page to be removed.
     */
    public void removeImagePage(String pageName) {
        server.removeContext("/" + pageName);
    }
}

