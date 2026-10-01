// VERDICT | T09 AOP / cross-cutting | BETTER: SPRING
// WHY: an @Aspect intercepts any bean method, including the service layer; ASP.NET endpoint filters and middleware only wrap HTTP requests.

using Shop.Api.T07_Transactions;
using Shop.Api.T09_Aop;

namespace Shop.Api.Tests.T09_Aop;

public class AopTests
{
    [Fact]
    public async Task FilterWrapsHttpEndpoints()
    {
        var factory = new ShopApiFactory();
        var client = factory.CreateClient();
        await client.PostAsJsonAsync("/api/products", new { sku = "Z2", name = "Mug", category = "Grocery", price = 6.50, stock = 4 });
        await client.PostAsJsonAsync("/api/orders", new OrderRequest("Eva", [new OrderLineRequest("X9", 1)]));

        var trail = factory.Services.GetRequiredService<AuditTrail>().Entries;
        Assert.Contains("RegisterProduct ok", trail);
        Assert.Contains("PlaceOrder failed: 404", trail);
    }

    [Fact]
    public async Task DirectServiceCallsBypassTheFilter()
    {
        var factory = new ShopApiFactory();
        factory.CreateClient();
        var trail = factory.Services.GetRequiredService<AuditTrail>();
        var before = trail.Entries.Count;

        using var scope = factory.Services.CreateScope();
        var orders = scope.ServiceProvider.GetRequiredService<OrderService>();
        await Assert.ThrowsAsync<UnknownProductException>(() =>
            orders.PlaceOrderAsync(new OrderRequest("Eva", [new OrderLineRequest("X9", 1)])));

        Assert.Equal(before, trail.Entries.Count); // not audited: no HTTP request, no filter
    }
}
