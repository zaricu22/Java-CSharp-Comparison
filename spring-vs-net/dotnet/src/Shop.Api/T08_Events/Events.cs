// VERDICT | T08 Events | BETTER: SPRING
// WHY: ApplicationEventPublisher + @EventListener / @TransactionalEventListener are built in; ASP.NET has no in-process event bus, so it is hand-written (or MediatR).

using System.Collections.Concurrent;

namespace Shop.Api.T08_Events;

public record OrderPlaced(int OrderId, string Customer, IReadOnlyList<string> Skus);

// ---- the event bus itself: ~15 lines that Spring provides out of the box ----

public interface IEventHandler<in TEvent>
{
    Task HandleAsync(TEvent @event, CancellationToken ct);
}

public interface IEventPublisher
{
    Task PublishAsync<TEvent>(TEvent @event, CancellationToken ct = default);
}

internal sealed class EventPublisher(IServiceProvider services) : IEventPublisher
{
    public async Task PublishAsync<TEvent>(TEvent @event, CancellationToken ct = default)
    {
        foreach (var handler in services.GetServices<IEventHandler<TEvent>>())
        {
            await handler.HandleAsync(@event, ct);
        }
    }
}

// ---- listeners ----

public sealed class StockAlerts
{
    private readonly ConcurrentQueue<string> _alerts = new();

    public void Add(string alert) => _alerts.Enqueue(alert);

    public IReadOnlyList<string> Alerts => _alerts.ToList();
}

internal sealed class StockAlertHandler(ShopDbContext db, StockAlerts alerts) : IEventHandler<OrderPlaced>
{
    private const int LowStock = 5;

    public async Task HandleAsync(OrderPlaced @event, CancellationToken ct)
    {
        foreach (var sku in @event.Skus)
        {
            if (await db.Products.FindAsync([sku], ct) is { Stock: < LowStock } product)
            {
                alerts.Add($"{sku} low: {product.Stock}");
            }
        }
    }
}

public sealed class OrderAuditLog
{
    private readonly ConcurrentQueue<string> _entries = new();

    public void Add(string entry) => _entries.Enqueue(entry);

    public IReadOnlyList<string> Entries => _entries.ToList();
}

internal sealed class OrderAuditHandler(OrderAuditLog log) : IEventHandler<OrderPlaced>
{
    public Task HandleAsync(OrderPlaced @event, CancellationToken ct)
    {
        log.Add($"order {@event.OrderId} by {@event.Customer}");
        return Task.CompletedTask;
    }
}

public static class EventsModule
{
    public static IServiceCollection AddShopEvents(this IServiceCollection services)
    {
        services.AddScoped<IEventPublisher, EventPublisher>();
        services.AddSingleton<StockAlerts>();
        services.AddSingleton<OrderAuditLog>();
        services.AddScoped<IEventHandler<OrderPlaced>, StockAlertHandler>();
        services.AddScoped<IEventHandler<OrderPlaced>, OrderAuditHandler>();
        return services;
    }
}
