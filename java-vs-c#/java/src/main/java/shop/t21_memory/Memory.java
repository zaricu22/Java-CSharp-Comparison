// VERDICT | T21 Memory & value types | BETTER: C#
// WHY: struct arrays are one allocation, and Span/stackalloc parse with zero allocations; Java allocates an object per element.

package shop.t21_memory;

import java.lang.management.ManagementFactory;

/**
 * Every user-defined type is a heap object. An array of 1M points is 1M references plus
 * 1M separate objects (each with a header) for the GC to track. The Java workaround for
 * hot data is "structure of arrays": parallel primitive arrays instead of objects.
 * (Project Valhalla value classes will change this, but they are not in Java 25.)
 */
public final class Memory {

    public record Point(double x, double y) {}

    /** Structure-of-arrays: no objects per point, but no Point type either. */
    public record Points(double[] xs, double[] ys) {

        public Points(int count) {
            this(new double[count], new double[count]);
        }
    }

    private static final com.sun.management.ThreadMXBean THREADS =
            (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();

    private Memory() {}

    public static long allocatedBytes() {
        return THREADS.getThreadAllocatedBytes(Thread.currentThread().threadId());
    }

    public static Point[] objectPoints(int count) {
        Point[] points = new Point[count];
        for (int i = 0; i < count; i++) {
            points[i] = new Point(i, i);
        }
        return points;
    }

    public static Points arrayPoints(int count) {
        Points points = new Points(count);
        for (int i = 0; i < count; i++) {
            points.xs()[i] = i;
            points.ys()[i] = i;
        }
        return points;
    }

    /** Allocation-free CSV parsing is possible, but only by hand-parsing digits (no Span, no stackalloc). */
    public static int csvSum(String line) {
        int sum = 0;
        int current = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == ',') {
                sum += current;
                current = 0;
            } else {
                current = current * 10 + (c - '0');
            }
        }
        return sum + current;
    }

    /** The idiomatic version allocates an array plus one String per field. */
    public static int csvSumWithSplit(String line) {
        int sum = 0;
        for (String part : line.split(",")) {
            sum += Integer.parseInt(part);
        }
        return sum;
    }
}
