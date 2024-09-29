package cs250.paint;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AutosaveTimerTest {

    @Test
    void oneHundredSecondsEquals1Min40Sec() {
        // Arrange
        int inputSeconds = 100;
        String expectedOutput = "01:40";  // 1 minute and 40 seconds

        // Act
        String actualOutput = AutosaveTimer.formatTime(inputSeconds);

        // Assert
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void negativeOneHundredSecondsEquals1Min40Sec() {
        // Arrange
        int inputSeconds = -100;
        String expectedOutput = "-1:-40";  // 1 minute and 40 seconds

        // Act
        String actualOutput = AutosaveTimer.formatTime(inputSeconds);

        // Assert
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    void thirtySixThousandSecEquals10Hours() {
        // Arrange
        int inputSeconds = 36000;
        String expectedOutput = "600:00";  // 1 minute and 40 seconds

        // Act
        String actualOutput = AutosaveTimer.formatTime(inputSeconds);

        // Assert
        assertEquals(expectedOutput, actualOutput);
    }

}