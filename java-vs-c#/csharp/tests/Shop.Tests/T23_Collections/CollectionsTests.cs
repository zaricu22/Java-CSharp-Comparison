// VERDICT | T23 Collection expressions | BETTER: C#
// WHY: one [a, ..b, ..c] syntax builds arrays, lists, sets and immutable collections; Java mixes List.of, addAll and streams, and List.of hides immutability until add() throws.

namespace Shop.T23_Collections;

public class CollectionsTests
{
    [Fact]
    public void CombineElementAndSequences()
    {
        var featured = CollectionLiterals.Featured(SampleData.Monitor, [SampleData.Coffee, SampleData.CleanCode], [SampleData.Refactoring]);
        Assert.Equal(["Monitor", "Coffee", "Clean Code", "Refactoring"], featured.Select(p => p.Name));
    }

    [Fact]
    public void MergeArrays()
    {
        Assert.Equal([2, 1, 3, 5], CollectionLiterals.MergeQuantities([2, 1], [3, 5]));
    }

    [Fact]
    public void MutableListCanGrow()
    {
        var tags = CollectionLiterals.DefaultTags();
        tags.Add("bestseller");
        Assert.Equal(["sale", "new", "bestseller"], tags);
    }

    [Fact]
    public void ImmutabilityIsVisibleInTheType()
    {
        var countries = CollectionLiterals.ShippingCountries;
        var extended = countries.Add("US");        // returns a NEW set - no exception, nothing mutated
        Assert.Contains("DE", countries);
        Assert.DoesNotContain("US", countries);
        Assert.Contains("US", extended);
    }

    [Fact]
    public void EmptyCollection()
    {
        Assert.Empty(CollectionLiterals.NoOrders());
    }
}
