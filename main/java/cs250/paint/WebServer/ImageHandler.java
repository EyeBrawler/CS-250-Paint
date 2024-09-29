package cs250.paint.WebServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import cs250.paint.CanvasTab;

/**
 * An implementation of the httpHandler class used by the PaintWebServer's httpServer. This implementation is
 * specifically designed to display an image.
 */
class ImageHandler implements HttpHandler {
    CanvasTab canvasTab;

    /**
     * Constructs an ImageHandler object. This single parameter constructor passes in a specific tab that will have its
     * snapshots eventually written to a web page on the web server.
     * @param canvasTab
     * A canvas tab that will have its snapshots of its canvas sent to the server.
     */
    public ImageHandler(CanvasTab canvasTab) {
        this.canvasTab = canvasTab;
    }

    /**
     * Handling the web server's client request on a new thread.
     * @param exchange
     * The exchange containing the request from the client for the server to send a response.
     */
    public void handle(HttpExchange exchange) {
        Thread imageHandlerTask = new Thread(new ImageHandlerTask(exchange, canvasTab.getBufferedImage(),
                canvasTab.getFileType(), canvasTab.isRequestedForServer()));

        imageHandlerTask.start();
    }
}
