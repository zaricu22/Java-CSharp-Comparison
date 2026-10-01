// VERDICT | T07 Events & functions | BETTER: TIE
// WHY: C#: events (+=, -=, owner-only raise) and closures that modify locals; Java: built-in composition (and/negate/andThen) and effectively-final capture.

namespace Shop.T07_Events;

public class EventsTests
{
    [Fact]
    public void ListenerIsNotifiedWhenStockDropsBelowThreshold()
    {
        var inventory = new Inventory(threshold: 5);
        var received = new List<StockLow>();
        inventory.StockRunningLow += received.Add;

        inventory.Add("G1", 10);
        inventory.Remove("G1", 3); // 7 left - no event
        inventory.Remove("G1", 4); // 3 left - event

        Assert.Equal([new StockLow("G1", 3)], received);
        Assert.Equal(3, inventory.StockOf("G1"));
    }

    [Fact]
    public void RemovedListenerIsNotNotified()
    {
        var inventory = new Inventory(threshold: 5);
        var received = new List<StockLow>();
        Action<StockLow> listener = received.Add; // must keep the same reference to unsubscribe
        inventory.StockRunningLow += listener;
        inventory.StockRunningLow -= listener;
        // inventory.StockRunningLow(new StockLow("X", 0));  -> compile error: only Inventory can raise it

        inventory.Add("G1", 1);
        inventory.Remove("G1", 1);

        Assert.Empty(received);
    }

    [Fact]
    public void LambdasCaptureAndModifyLocals()
    {
        var inventory = new Inventory(threshold: 5);
        var count = 0;
        inventory.StockRunningLow += _ => count++; // closures capture the variable itself
        inventory.Add("G1", 2);
        inventory.Remove("G1", 1);
        inventory.Remove("G1", 1);
        Assert.Equal(2, count);
    }

    [Fact]
    public void ForLoopVariableIsSharedByClosures()
    {
        var actions = new List<Func<int>>();
        for (var i = 0; i < 3; i++)
        {
            actions.Add(() => i); // compiles - all three lambdas share the same i
        }
        Assert.Equal([3, 3, 3], actions.Select(a => a())); // the classic trap (foreach is safe since C# 5)
    }

    [Fact]
    public void PredicatesComposeByHand()
    {
        var products = SampleData.Products;
        Assert.Equal(["Keyboard", "Monitor"], ProductFilters.Names(products, ProductFilters.ExpensiveNonBook));
        Assert.Equal(["Clean Code", "Refactoring", "Coffee"], ProductFilters.Names(products, ProductFilters.BookOrCheap));
    }

    [Fact]
    public void FunctionsComposeByNesting()
    {
        Assert.Equal(107.99m, ProductFilters.GrossPrice(89.99m));
    }
}
