// VERDICT | T07 Events & functions | BETTER: TIE
// WHY: C#: events (+=, -=, owner-only raise) and closures that modify locals; Java: built-in composition (and/negate/andThen) and effectively-final capture.

namespace Shop.T07_Events;

public record StockLow(string Sku, int Remaining);

/// <summary>
/// "event" is a language feature: subscribe with +=, unsubscribe with -=, raise null-safely
/// with ?.Invoke - and only the owning class can raise it (outside code can only += / -=).
/// </summary>
public sealed class Inventory(int threshold)
{
    private readonly Dictionary<string, int> _stock = [];

    public event Action<StockLow>? StockRunningLow;

    public void Add(string sku, int quantity) => _stock[sku] = _stock.GetValueOrDefault(sku) + quantity;

    public void Remove(string sku, int quantity)
    {
        var left = _stock[sku] = _stock.GetValueOrDefault(sku) - quantity;
        if (left < threshold)
        {
            StockRunningLow?.Invoke(new StockLow(sku, left));
        }
    }

    public int StockOf(string sku) => _stock.GetValueOrDefault(sku);
}

/// <summary>
/// Where Java wins: Func/Predicate delegates have no built-in And/Or/Negate/AndThen.
/// You compose with a new lambda (fine for small cases) or write your own extension methods.
/// </summary>
public static class ProductFilters
{
    public static readonly Func<Product, bool> IsBook = p => p.Category == Category.Books;
    public static readonly Func<Product, bool> IsCheap = p => p.Price < 40m;

    public static readonly Func<Product, bool> ExpensiveNonBook = p => !IsBook(p) && !IsCheap(p);
    public static readonly Func<Product, bool> BookOrCheap = p => IsBook(p) || IsCheap(p);

    public static readonly Func<decimal, decimal> AddVat = p => p * 1.20m;
    public static readonly Func<decimal, decimal> Round = p => Math.Round(p, 2);
    public static readonly Func<decimal, decimal> GrossPrice = p => Round(AddVat(p));

    public static List<string> Names(IEnumerable<Product> products, Func<Product, bool> filter) =>
        products.Where(filter).Select(p => p.Name).ToList();
}
