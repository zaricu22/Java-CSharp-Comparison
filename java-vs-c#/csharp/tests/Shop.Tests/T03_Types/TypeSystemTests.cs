// VERDICT | T03 Type system & generics | BETTER: C#
// WHY: reified generics (typeof(T), new T(), List<int> without boxing) and generic math; Java has erasure and Integer boxing traps.

namespace Shop.T03_Types;

public class TypeSystemTests
{
    [Fact]
    public void PrimitivesHaveMethods()
    {
        Assert.Equal("255 units (0xff)", TypeSystem.DescribeStock(255));
    }

    [Fact]
    public void NumbersCompareByValue()
    {
        int a = 1000, b = 1000;
        Assert.True(a == b);
        // Boxing still exists, but only when you explicitly go through object:
        object boxedA = a, boxedB = b;
        Assert.False(boxedA == boxedB);
    }

    [Fact]
    public void StringEqualityComparesValues()
    {
        var sku = new string("B1".AsSpan()); // a distinct string instance, like one read from a request
        Assert.False(ReferenceEquals(sku, "B1"));
        Assert.True(sku == "B1");            // string overloads == to compare values
    }

    [Fact]
    public void ValueTypeListCannotHoldNulls()
    {
        // TypeSystem.TotalUnits([2, null, 3]);  -> compile error: null is not an int
        Assert.Equal(6, TypeSystem.TotalUnits([2, 1, 3]));
    }

    [Fact]
    public void GenericMathWorksForAnyNumber()
    {
        Assert.Equal(6, TypeSystem.Sum([1, 2, 3]));
        Assert.Equal(3.30m, TypeSystem.Sum([1.10m, 2.20m]));
        Assert.Equal(3.5, TypeSystem.Sum([1.5, 2.0]));
    }

    [Fact]
    public void RepositoryKnowsItsTypeAtRuntime()
    {
        var repo = new Repository<CartDraft>();
        var draft = repo.CreateNew();
        Assert.Equal("CartDraft", repo.EntityName);
        Assert.Equal("new cart", draft.Note);
        Assert.Equal(1, repo.Count);
    }

    [Fact]
    public void ReifiedGenericsTellEmptyListTypes()
    {
        Assert.True(TypeSystem.IsListOfStrings(new List<string> { "a", "b" }));
        Assert.False(TypeSystem.IsListOfStrings(new List<int> { 1, 2 }));
        Assert.False(TypeSystem.IsListOfStrings(new List<int>())); // correct even when empty
    }
}
