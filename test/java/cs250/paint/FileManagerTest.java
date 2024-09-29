package cs250.paint;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileManagerTest {

    @Test
    void extractsJpgFileExtension() {
        assertEquals("jpg", FileManager.getFileExtension("RandomFile.jpg"));
    }

    @Test
    void extractsBmpFileExtension() {
        assertEquals("bmp", FileManager.getFileExtension("RandomFile.bmp"));
    }

    @Test
    void extractsPngFileExtension() {
        assertEquals("png", FileManager.getFileExtension("RandomFile.png"));
    }

    @Test
    void extractsNoFileExtension() {
        assertNull(FileManager.getFileExtension("RandomFile"));
    }

    @Test
    void extractsMiddleDotPngFileExtension() {
        assertEquals("png", FileManager.getFileExtension("Random.File.png"));
    }

}