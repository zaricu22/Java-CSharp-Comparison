// VERDICT | T21 Memory & value types | BETTER: C#
// WHY: struct arrays are one allocation, and Span/stackalloc parse with zero allocations; Java allocates an object per element.

namespace Shop.T21_Memory;

public class MemoryTests
{
    private const int Count = 1_000_000;

    [Fact]
    public void StructArrayIsOneAllocation()
    {
        var before = Memory.AllocatedBytes();
        var points = Memory.StructPoints(Count);
        var perPoint = (Memory.AllocatedBytes() - before) / Count;

        Assert.Equal(Count, points.Length);
        Assert.True(perPoint <= 17, $"per point: {perPoint}"); // exactly two doubles, no headers
    }

    [Fact]
    public void SpanParsingAllocatesNothing()
    {
        Memory.CsvSum("1,2"); // warm-up (JIT, static init)
        var before = Memory.AllocatedBytes();
        var sum = Memory.CsvSum("12,30,8");
        var allocated = Memory.AllocatedBytes() - before;

        Assert.Equal(50, sum);
        Assert.Equal(0, allocated);
    }

    [Fact]
    public void CsvParsingBothWays()
    {
        Assert.Equal(50, Memory.CsvSum("12,30,8"));
        Assert.Equal(50, Memory.CsvSumWithSplit("12,30,8"));
    }
}
