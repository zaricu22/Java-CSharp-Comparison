// VERDICT | T18 Declarative HTTP clients | BETTER: SPRING
// WHY: an annotated interface becomes a working client (@HttpExchange) and MockRestServiceServer tests it; .NET writes typed HttpClient code by hand (Refit is third-party) and ships no mock server.

namespace Shop.Api.T18_HttpClients;

public record SupplierStock(string Sku, int Available);

public record SupplierOrderRequest(string Sku, int Quantity);

public record SupplierOrder(string Reference);

/// <summary>
/// A "typed client": IHttpClientFactory manages the HttpClient (pooling, DNS refresh, resilience
/// handlers), but every call - URL, escaping, JSON, status checks - is written by hand.
/// Spring generates all of this from an annotated interface.
/// </summary>
public sealed class SupplierClient(HttpClient http)
{
    public async Task<SupplierStock> StockAsync(string sku, CancellationToken ct = default) =>
        await http.GetFromJsonAsync<SupplierStock>($"supplier/stock/{Uri.EscapeDataString(sku)}", ct)
        ?? throw new InvalidOperationException("Supplier returned an empty body");

    public async Task<SupplierOrder> OrderAsync(SupplierOrderRequest request, CancellationToken ct = default)
    {
        using var response = await http.PostAsJsonAsync("supplier/orders", request, ct);
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadFromJsonAsync<SupplierOrder>(ct)
               ?? throw new InvalidOperationException("Supplier returned an empty body");
    }
}

public record ReorderResult(string Sku, int Quantity, string Reference);

public class SupplierOutOfStockException(string sku, int available) : Exception($"Supplier has only {available} of {sku}");

public sealed class ReorderService(SupplierClient supplier)
{
    public async Task<ReorderResult> ReorderAsync(string sku, int quantity, CancellationToken ct = default)
    {
        var stock = await supplier.StockAsync(sku, ct);
        if (stock.Available < quantity)
        {
            throw new SupplierOutOfStockException(sku, stock.Available);
        }
        var order = await supplier.OrderAsync(new SupplierOrderRequest(sku, quantity), ct);
        return new ReorderResult(sku, quantity, order.Reference);
    }
}

public static class SupplierModule
{
    public static IServiceCollection AddSupplierClient(this IServiceCollection services, IConfiguration configuration)
    {
        // The trailing "/" matters: without it, relative paths replace the last segment of BaseAddress.
        services.AddHttpClient<SupplierClient>(http => http.BaseAddress = new Uri(configuration["Shop:Supplier:BaseUrl"]!));
        services.AddScoped<ReorderService>();
        return services;
    }
}
