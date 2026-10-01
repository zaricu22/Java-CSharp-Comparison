// VERDICT | T16 Anonymous & inner classes | BETTER: JAVA
// WHY: anonymous classes implement multi-method interfaces inline and inner classes see the outer instance; C# needs named classes.

namespace Shop.T16_InnerClasses;

public class InnerClassesTests
{
    [Fact]
    public void NestedItemReadsCartThroughExplicitReference()
    {
        var cart = new ShoppingCart();
        var books = cart.Add(SampleData.CleanCode, 2);
        var keyboard = cart.Add(SampleData.Keyboard, 1);

        Assert.Equal(160.99m, cart.Subtotal);
        Assert.Equal(55.9m, keyboard.ShareOfCart);
        Assert.Equal(44.1m, books.ShareOfCart);
    }

    [Fact]
    public void NestedItemChangesCart()
    {
        var cart = new ShoppingCart();
        var books = cart.Add(SampleData.CleanCode, 2);
        var keyboard = cart.Add(SampleData.Keyboard, 1);

        books.Increase();
        keyboard.Remove();

        Assert.Equal(1, cart.Count);
        Assert.Equal(106.50m, cart.Subtotal);
    }

    [Fact]
    public void NamedRuleKeepsItsOwnState()
    {
        var rule = Discounts.OneTimePercent(100m, 10);
        var subtotal = 160.99m;

        Assert.Equal("10% once over 100", rule.Name);
        Assert.Equal(144.89m, rule.Apply(subtotal));
        Assert.Equal(subtotal, rule.Apply(subtotal)); // already used
        Assert.Equal(50m, Discounts.OneTimePercent(100m, 10).Apply(50m));
    }
}
