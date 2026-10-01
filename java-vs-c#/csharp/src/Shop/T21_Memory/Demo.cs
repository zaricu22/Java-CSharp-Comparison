// VERDICT | T21 Memory & value types | BETTER: C#
// WHY: struct arrays are one allocation, and Span/stackalloc parse with zero allocations; Java allocates an object per element.

namespace Shop.T21_Memory;

public static class Demo
{
    public static void Run()
    {
        const int count = 1_000_000;

        var before = Memory.AllocatedBytes();
        var points = Memory.StructPoints(count);
        var structBytes = Memory.AllocatedBytes() - before;

        Memory.CsvSum("1,2"); // warm-up
        before = Memory.AllocatedBytes();
        var sum = Memory.CsvSum("12,30,8");
        var parseBytes = Memory.AllocatedBytes() - before;

        Console.WriteLine($"1M Point structs: {structBytes:N0} bytes ({structBytes / count} per point)");
        Console.WriteLine($"CSV sum: {sum} / {Memory.CsvSumWithSplit("12,30,8")} (span version allocated {parseBytes} bytes)");
        Console.WriteLine($"(kept alive: {points.Length})");
    }
}
