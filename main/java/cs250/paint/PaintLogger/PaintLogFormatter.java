package cs250.paint.PaintLogger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * A custom formatter class used for formatting the entries of the Pain(t) log.
 */
public class PaintLogFormatter extends Formatter {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     *
     * @param record the log record to be formatted.
     * @return the formatted string for a logged event.
     */
    public String format(LogRecord record) {
        // Get current date and time
        String dateTime = dateFormat.format(new Date(record.getMillis()));

        // Extract the message (which will include the file and the action)
        String message = record.getMessage();

        // Create the formatted log entry: date, time, file, and message
        return dateTime + " - " + message + "\n";
    }
}
