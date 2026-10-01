// VERDICT | T22 Resource cleanup | BETTER: TIE
// WHY: C# `using var` needs no nesting; Java try-with-resources keeps the original exception and records close() failures as suppressed.

namespace Shop.T22_Resources;

public class ResourcesTests
{
    [Fact]
    public void ResourcesCloseInReverseOrder()
    {
        var journal = new List<string>();
        Exporter.ExportOrders(journal, "1,Ana", false);
        Assert.Equal(["orders.csv: opened", "lines.csv: opened", "orders.csv: 1,Ana",
            "lines.csv: 1,B1,2", "lines.csv: closed", "orders.csv: closed"], journal);
    }

    [Fact]
    public void ResourcesCloseWhenBodyFails()
    {
        var journal = new List<string>();
        Assert.Throws<InvalidOperationException>(() => Exporter.ExportOrders(journal, "", false));
        Assert.Equal(["orders.csv: opened", "lines.csv: opened", "lines.csv: closed", "orders.csv: closed"], journal);
    }

    [Fact]
    public void OriginalExceptionIsLostWhenDisposeAlsoFails()
    {
        var ex = Assert.Throws<InvalidOperationException>(() => Exporter.ExportOrders([], "", true));
        Assert.Equal("close failed", ex.Message); // "write failed" - the real cause - is gone
    }
}
