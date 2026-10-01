// VERDICT | T08 Async | BETTER: TIE
// WHY: C# async/await beats CompletableFuture; Java 21+ virtual threads allow plain blocking code with no async 'coloring'.

namespace Shop.T08_Async;

public record ProductView(string Sku, decimal Price, int Stock);

/// <summary>Fake "remote" services: every call awaits <see cref="Latency"/> like a slow HTTP request.</summary>
public static class RemoteApis
{
    public static readonly TimeSpan Latency = TimeSpan.FromMilliseconds(100);

    private static readonly Dictionary<string, decimal> Prices = new() { ["B1"] = 35.50m, ["E1"] = 89.99m, ["G1"] = 12.40m };
    private static readonly Dictionary<string, int> Stock = new() { ["B1"] = 12, ["E1"] = 3, ["G1"] = 40 };

    public static async Task<decimal> PriceAsync(string sku, CancellationToken ct = default)
    {
        await Task.Delay(Latency, ct);
        return Prices.TryGetValue(sku, out var price) ? price : throw new KeyNotFoundException($"Unknown sku {sku}");
    }

    public static async Task<int> StockAsync(string sku, CancellationToken ct = default)
    {
        await Task.Delay(Latency, ct);
        return Stock.GetValueOrDefault(sku);
    }
}

/// <summary>
/// async/await: asynchronous code written like sequential code. Exceptions come out
/// unwrapped into a normal try/catch, and cancellation is a standard CancellationToken.
/// (The cost: "async" spreads through every caller's signature - Java's virtual threads avoid that.)
/// </summary>
public static class CatalogService
{
    public static async Task<ProductView> ViewAsync(string sku, CancellationToken ct = default)
    {
        var price = RemoteApis.PriceAsync(sku, ct); // both calls start now...
        var stock = RemoteApis.StockAsync(sku, ct);
        return new ProductView(sku, await price, await stock); // ...and run at the same time
    }

    public static Task<ProductView[]> ViewsAsync(IEnumerable<string> skus, CancellationToken ct = default) =>
        Task.WhenAll(skus.Select(sku => ViewAsync(sku, ct)));

    public static async Task<decimal> PriceOrDefaultAsync(string sku, decimal fallback)
    {
        try
        {
            return await RemoteApis.PriceAsync(sku);
        }
        catch (KeyNotFoundException)
        {
            return fallback;
        }
    }
}
