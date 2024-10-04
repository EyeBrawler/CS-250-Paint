package cs250.paint.PaintLogger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class PaintLogger {
    private static final Logger logger = Logger.getLogger(PaintLogger.class.getName());
    private static FileHandler fileHandler;
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    static {
        try {
            // Get the absolute path to the project root directory
            String projectRoot = System.getProperty("user.dir");
            // Define a proper logs directory outside the resources folder
            File logDir = new File(projectRoot, "logs");
            if (!logDir.exists()) {
                boolean dirCreated = logDir.mkdirs();  // Try to create the logs directory
                if (!dirCreated) {
                    logger.warning("Failed to create directory for logs: " + logDir.getAbsolutePath());
                }
            }

            // Initialize FileHandler to log into a text file in the logs directory
            fileHandler = new FileHandler(new File(logDir, "paint_operations.log").getAbsolutePath(), true);
            fileHandler.setFormatter(new PaintLogFormatter());  // Use custom formatter
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);  // Disable console logging

        } catch (IOException e) {
            logger.severe("Failed to initialize file handler for logging: " + e.getMessage());
        }
    }

    // Method to log operations asynchronously
    public static void logOperation(String fileName, String operation) {
        String message = "File: " + fileName + " - " + operation;
        executor.submit(() -> logger.info(message));
    }

    // Clean up resources
    public static void shutdownLogger() {
        executor.shutdown();
        fileHandler.close();
    }
}
