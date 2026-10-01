// VERDICT | T24 Labeled break & continue | BETTER: JAVA
// WHY: `break search;` / `continue orders;` leave or skip an outer loop directly; C# needs goto, a flag variable or an extra method.

namespace Shop.T24_Loops;

public class LoopControlTests
{
    [Fact]
    public void BreakOutOfBothLoops()
    {
        Assert.Equal("Blocked order 1: Keyboard", LoopControl.FirstUnavailable(SampleData.Orders, new HashSet<string> { "E1" }));
        Assert.Equal("Blocked order 2: Coffee", LoopControl.FirstUnavailable(SampleData.Orders, new HashSet<string> { "G1" }));
        Assert.Equal("all available", LoopControl.FirstUnavailable(SampleData.Orders, new HashSet<string>()));
    }

    [Fact]
    public void ContinueTheOuterLoop()
    {
        Assert.Equal([2, 3], LoopControl.ShippableOrders(SampleData.Orders, new HashSet<string> { "E1" }));
        Assert.Equal([1, 2, 3, 4], LoopControl.ShippableOrders(SampleData.Orders, new HashSet<string>()));
    }
}
