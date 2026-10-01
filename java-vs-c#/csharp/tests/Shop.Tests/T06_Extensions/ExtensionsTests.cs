// VERDICT | T06 Extension methods | BETTER: C#
// WHY: extension methods/properties read left-to-right on the object; Java needs static Utils classes called inside-out.

namespace Shop.T06_Extensions;

public class ExtensionsTests
{
    [Fact]
    public void Truncate()
    {
        Assert.Equal("Refactoring...", "Refactoring improves design".Truncate(11));
        Assert.Equal("Short", "Short".Truncate(11));
    }

    [Fact]
    public void Slug()
    {
        Assert.Equal("clean-code-a-handbook-of-agile-craftsmanship",
            "  Clean Code: A Handbook of Agile Craftsmanship! ".ToSlug());
    }

    [Fact]
    public void ChainedOrderFilters()
    {
        var orders = SampleData.Orders;
        Assert.Equal(160.99m, orders.PlacedIn(2026, 1).From("Belgrade").Revenue);
        Assert.Equal(731.17m, orders.Revenue);
    }
}
