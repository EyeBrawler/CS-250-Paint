package cs250.paint.PaintTools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RightTriangleToolTest {
    private RightTriangleTool rightTriangleTool;

    @BeforeEach
    public void setUp() {
        rightTriangleTool = new RightTriangleTool();
        // Simulate the mouse press by setting the first point
        rightTriangleTool.xPoints[0] = 100.0;
        rightTriangleTool.yPoints[0] = 100.0;
    }

    @Test
    public void testCalculateRightTriangle() {
        // Call the method with current mouse position
        rightTriangleTool.calculateRightTriangle(200.0, 150.0);

        // Assert that the points of the triangle are calculated correctly
        assertEquals(200.0, rightTriangleTool.xPoints[1], "Second point X coordinate mismatch");
        assertEquals(150.0, rightTriangleTool.yPoints[1], "Second point Y coordinate mismatch");
        assertEquals(100.0, rightTriangleTool.xPoints[2], "Third point X coordinate mismatch");
        assertEquals(150.0, rightTriangleTool.yPoints[2], "Third point Y coordinate mismatch");
    }
}
