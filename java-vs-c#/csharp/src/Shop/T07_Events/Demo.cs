// VERDICT | T07 Events & functions | BETTER: TIE
// WHY: C#: events (+=, -=, owner-only raise) and closures that modify locals; Java: built-in composition (and/negate/andThen) and effectively-final capture.

namespace Shop.T07_Events;

public static class Demo
{
    public static void Run()
    {
        var inventory = new Inventory(threshold: 5);
        inventory.StockRunningLow += e => Console.WriteLine($"Reorder {e.Sku}, only {e.Remaining} left");
        inventory.Add("G1", 10);
        inventory.Remove("G1", 3);
        inventory.Remove("G1", 4);

        var products = SampleData.Products;
        Console.WriteLine($"Expensive non-books: [{string.Join(", ", ProductFilters.Names(products, ProductFilters.ExpensiveNonBook))}]");
        Console.WriteLine($"Gross 89.99: {ProductFilters.GrossPrice(89.99m)}");
    }
}
