// VERDICT | T21 Memory & value types | BETTER: C#
// WHY: struct arrays are one allocation, and Span/stackalloc parse with zero allocations; Java allocates an object per element.

namespace Shop.T21_Memory;

/// <summary>
/// A struct array stores the values inline: 1M points is ONE allocation of 16 MB with no
/// per-element headers or references for the GC. Span&lt;T&gt; and stackalloc give
/// allocation-free slicing and parsing in fully safe code (no "unsafe" needed).
/// </summary>
public static class Memory
{
    public readonly record struct Point(double X, double Y);

    public static long AllocatedBytes() => GC.GetAllocatedBytesForCurrentThread();

    public static Point[] StructPoints(int count)
    {
        var points = new Point[count];
        for (var i = 0; i < count; i++)
        {
            points[i] = new Point(i, i);
        }
        return points;
    }

    /// <summary>Split into a stack-allocated buffer of ranges, parse each slice: zero heap allocations.</summary>
    public static int CsvSum(ReadOnlySpan<char> line)
    {
        Span<Range> fields = stackalloc Range[16];
        var count = line.Split(fields, ',');
        var sum = 0;
        foreach (var field in fields[..count])
        {
            sum += int.Parse(line[field]);
        }
        return sum;
    }

    /// <summary>The same convenient string.Split version as Java, for comparison.</summary>
    public static int CsvSumWithSplit(string line) => line.Split(',').Sum(s => int.Parse(s));
}
