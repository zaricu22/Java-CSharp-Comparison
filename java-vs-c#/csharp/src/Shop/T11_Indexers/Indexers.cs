// VERDICT | T11 Indexers & ranges | BETTER: C#
// WHY: catalog["E1"], matrix[1, 1], items[1..^1]; Java spells everything as get()/subList()/substring().

namespace Shop.T11_Indexers;

/// <summary>Indexers give a class [] access - overloaded by key type, with get and set.</summary>
public sealed class Catalog(IEnumerable<Product> products)
{
    private readonly Dictionary<string, Product> _bySku = products.ToDictionary(p => p.Sku);

    public Product this[string sku]
    {
        get => _bySku.TryGetValue(sku, out var product) ? product : throw new KeyNotFoundException($"Unknown sku {sku}");
        set => _bySku[sku] = value;
    }

    public IReadOnlyList<Product> this[Category category] =>
        _bySku.Values.Where(p => p.Category == category).ToList();
}

/// <summary>A 2-D indexer over a true rectangular array (decimal[,]).</summary>
public sealed class ShippingMatrix
{
    private readonly decimal[,] _prices =
    {
        { 2.50m, 5.00m, 12.00m },  // zone 0: domestic
        { 9.90m, 14.90m, 19.90m }, // zone 1: EU
        { 19.90m, 29.90m, 49.90m } // zone 2: world
    };

    public decimal this[int zone, int weightClass]
    {
        get => _prices[zone, weightClass];
        set => _prices[zone, weightClass] = value;
    }
}

/// <summary>Index-from-end (^1) and ranges (a..b) on arrays, strings and spans.</summary>
public static class Ranges
{
    public static T[] LastN<T>(T[] items, int n) => items[^Math.Min(n, items.Length)..];

    public static T[] WithoutFirstAndLast<T>(T[] items) => items[1..^1];

    public static string SkuPrefix(string sku) => sku[..1];

    public static string LastChars(string s, int n) => s[^n..];
}
