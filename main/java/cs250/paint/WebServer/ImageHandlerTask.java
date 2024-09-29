package cs250.paint.WebServer;

import com.sun.net.httpserver.HttpExchange;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ImageHandlerTask implements Runnable {
    private final BufferedImage bufferedImage;
    private final HttpExchange exchange;
    private final String fileExtension;
    private final boolean imageRequested;

    /**
     * Initializes a ImageHandlerTask with the variables necessary to write the image to a webpage on a separate thread
     * for the user.
     * @param exchange
     * The exchange object used and needed by the web server.
     * @param bufferedImage
     * The BufferedImage collected in the tab from saving operations.
     * @param fileExtension
     * The extension of the buffered image.
     * @param imageRequested
     * If flag for if the Canvas has requested the image
     */
    public ImageHandlerTask(HttpExchange exchange, BufferedImage bufferedImage, String fileExtension,
                            boolean imageRequested) {
        this.exchange = exchange;
        this.bufferedImage = bufferedImage;
        this.fileExtension = fileExtension;
        this.imageRequested = imageRequested;
    }

    public void run() {
        try {
            if(bufferedImage == null || !imageRequested) {
                // If the image is null, send a message instead of the image
                String noImageMessage = "No tab is currently available with an image.";

                // Set response headers for plain text or HTML
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                exchange.sendResponseHeaders(200, noImageMessage.length());

                //Using the output stream to write the exchange to the page
                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(noImageMessage.getBytes());
                outputStream.close();

            } else {
                //Converting the buffered image to a byte array for writing to the webpage
                ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, fileExtension, byteArrayOutput);
                byte[] imageBytes = byteArrayOutput.toByteArray();

                //Configuring the exchange
                exchange.getResponseHeaders().set("Content-Type", "image/" + fileExtension);
                exchange.sendResponseHeaders(200, imageBytes.length);

                //Writing the data for the exchange
                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(imageBytes);
                outputStream.close();
            }

        } catch (RuntimeException e) {
            System.err.println("Runtime exception occurred: " + e.getMessage());

            try {
                // Send a 500 response indicating a server error
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
            } catch (IOException ioException) {
                System.err.println("Failed to send 500 response: " + ioException.getMessage());
            }

            //Catching any other IO errors that occur during the exchange
        } catch (IOException e) {
            System.err.println("IOException occurred: " + e.getMessage());
            throw new RuntimeException(e);
        }

    }
}
