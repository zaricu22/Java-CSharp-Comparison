// VERDICT | T11 Indexers & ranges | BETTER: C#
// WHY: catalog["E1"], matrix[1, 1], items[1..^1]; Java spells everything as get()/subList()/substring().

namespace Shop.T11_Indexers;

public class IndexersTests
{
    [Fact]
    public void LookupBySkuAndCategory()
    {
        var catalog = new Catalog(SampleData.Products);
        Assert.Equal("Keyboard", catalog["E1"].Name);
        Assert.Equal(["Clean Code", "Refactoring"], catalog[Category.Books].Select(p => p.Name));
        Assert.Throws<KeyNotFoundException>(() => catalog["X9"]);
    }

    [Fact]
    public void ReplaceBySku()
    {
        var catalog = new Catalog(SampleData.Products);
        catalog["E1"] = new Product("E1", "Mechanical Keyboard", Category.Electronics, 129.00m);
        Assert.Equal("Mechanical Keyboard", catalog["E1"].Name);
    }

    [Fact]
    public void TwoDimensionalLookup()
    {
        var matrix = new ShippingMatrix();
        Assert.Equal(14.90m, matrix[1, 1]);
        matrix[1, 1] = 13.90m;
        Assert.Equal(13.90m, matrix[1, 1]);
    }

    [Fact]
    public void RangesFromTheEnd()
    {
        string[] skus = ["B1", "B2", "E1", "E2", "G1"];
        Assert.Equal(["E2", "G1"], Ranges.LastN(skus, 2));
        Assert.Equal(["B2", "E1", "E2"], Ranges.WithoutFirstAndLast(skus));
        Assert.Equal("B", Ranges.SkuPrefix("B12"));
        Assert.Equal("12", Ranges.LastChars("B12", 2));
    }
}
