// VERDICT | T08 Events | BETTER: SPRING
// WHY: ApplicationEventPublisher + @EventListener / @TransactionalEventListener are built in; ASP.NET has no in-process event bus, so it is hand-written (or MediatR).

using Shop.Api.T07_Transactions;
using Shop.Api.T08_Events;

namespace Shop.Api.Tests.T08_Events;

public class EventsTests
{
    [Fact]
    public async Task ListenersReactToPlacedOrder()
    {
        var factory = new ShopApiFactory();
        var response = await factory.CreateClient().PostAsJsonAsync("/api/orders",
            new OrderRequest("Eva", [new OrderLineRequest("E1", 1)]));
        var confirmation = await response.Content.ReadFromJsonAsync<OrderConfirmation>();

        Assert.Contains("E1 low: 2", factory.Services.GetRequiredService<StockAlerts>().Alerts);
        Assert.Contains($"order {confirmation!.OrderId} by Eva", factory.Services.GetRequiredService<OrderAuditLog>().Entries);
    }

    [Fact]
    public async Task FailedOrderPublishesNothing()
    {
        var factory = new ShopApiFactory();
        await factory.CreateClient().PostAsJsonAsync("/api/orders",
            new OrderRequest("Failing", [new OrderLineRequest("E2", 1)]));

        Assert.DoesNotContain(factory.Services.GetRequiredService<OrderAuditLog>().Entries, e => e.EndsWith("by Failing"));
    }
}
