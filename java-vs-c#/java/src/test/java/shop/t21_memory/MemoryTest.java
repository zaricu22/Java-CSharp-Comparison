// VERDICT | T21 Memory & value types | BETTER: C#
// WHY: struct arrays are one allocation, and Span/stackalloc parse with zero allocations; Java allocates an object per element.

package shop.t21_memory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MemoryTest {

    private static final int COUNT = 1_000_000;

    @Test
    void objectArrayAllocatesOneObjectPerElement() {
        long before = Memory.allocatedBytes();
        var points = Memory.objectPoints(COUNT);
        long perPoint = (Memory.allocatedBytes() - before) / COUNT;

        assertEquals(COUNT, points.length);
        // 4-8 bytes reference + ~24-32 bytes object (header + two doubles)
        assertTrue(perPoint >= 28, "per point: " + perPoint);
    }

    @Test
    void parallelPrimitiveArraysAreTheJavaWorkaround() {
        long before = Memory.allocatedBytes();
        var points = Memory.arrayPoints(COUNT);
        long perPoint = (Memory.allocatedBytes() - before) / COUNT;

        assertEquals(COUNT, points.xs().length);
        assertTrue(perPoint <= 17, "per point: " + perPoint); // two doubles, no headers
    }

    @Test
    void csvParsingBothWays() {
        assertEquals(50, Memory.csvSum("12,30,8"));
        assertEquals(50, Memory.csvSumWithSplit("12,30,8"));
    }
}
