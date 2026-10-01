// VERDICT | T21 Memory & value types | BETTER: C#
// WHY: struct arrays are one allocation, and Span/stackalloc parse with zero allocations; Java allocates an object per element.

package shop.t21_memory;

public final class Demo {

    public static void run() {
        int count = 1_000_000;

        long before = Memory.allocatedBytes();
        var objects = Memory.objectPoints(count);
        long objectBytes = Memory.allocatedBytes() - before;

        before = Memory.allocatedBytes();
        var arrays = Memory.arrayPoints(count);
        long arrayBytes = Memory.allocatedBytes() - before;

        System.out.printf("1M Point records: %,d bytes (%d per point)%n", objectBytes, objectBytes / count);
        System.out.printf("1M points as double[] pairs: %,d bytes (%d per point)%n", arrayBytes, arrayBytes / count);
        System.out.println("CSV sum: " + Memory.csvSum("12,30,8") + " / " + Memory.csvSumWithSplit("12,30,8"));
        System.out.println("(kept alive: " + objects.length + ", " + arrays.xs().length + ")");
    }
}
