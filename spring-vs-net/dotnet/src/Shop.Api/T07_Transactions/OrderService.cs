// VERDICT | T07 Transactions | BETTER: SPRING
// WHY: @Transactional makes a whole service method atomic across repositories and nested calls; EF's SaveChanges is one unit of work, several saves need BeginTransaction/Commit.

using System.ComponentModel.DataAnnotations;
using Microsoft.AspNetCore.Http.HttpResults;
using Shop.Api.T08_Events;
using Shop.Api.T09_Aop;

namespace Shop.Api.T07_Transactions;

public record OrderLineRequest([Required] string Sku, [Range(1, int.MaxValue)] int Quantity);

public record OrderRequest([Required] string Customer, [Required, MinLength(1)] List<OrderLineRequest> Lines);

public record OrderConfirmation(int OrderId, decimal Total);

public class UnknownProductException(string sku) : Exception($"Unknown product {sku}");

public sealed class OrderService(ShopDbContext db, IEventPublisher events)
{
    /// <summary>
    /// EF tracks every change and SaveChanges writes them in ONE transaction, so a failure before
    /// the save leaves the database untouched. Here the work needs more than one save (the order
    /// id is needed for the event), so the transaction has to be opened and committed by hand.
    /// </summary>
    public async Task<OrderConfirmation> PlaceOrderAsync(OrderRequest request, CancellationToken ct = default)
    {
        await using var transaction = await db.Database.BeginTransactionAsync(ct);

        var order = new Order { Customer = request.Customer, PlacedOn = DateOnly.FromDateTime(DateTime.Today) };
        foreach (var line in request.Lines)
        {
            var product = await db.Products.FindAsync([line.Sku], ct) ?? throw new UnknownProductException(line.Sku);
            product.RemoveStock(line.Quantity); // may throw InsufficientStockException -> nothing is saved
            order.AddLine(product, line.Quantity);
        }
        db.Orders.Add(order);
        await db.SaveChangesAsync(ct);

        await transaction.CommitAsync(ct);

        // No "after commit" listener exists: publishing after CommitAsync is the manual equivalent
        await events.PublishAsync(new OrderPlaced(order.Id, order.Customer, order.Lines.Select(l => l.ProductSku).ToList()), ct);
        return new OrderConfirmation(order.Id, order.Total);
    }
}

public static class OrderModule
{
    public static IServiceCollection AddOrders(this IServiceCollection services) => services.AddScoped<OrderService>();

    public static IEndpointRouteBuilder MapOrderEndpoints(this IEndpointRouteBuilder app)
    {
        app.MapPost("/api/orders", async Task<Results<Created<OrderConfirmation>, ProblemHttpResult>> (OrderRequest request, OrderService orders) =>
            {
                try
                {
                    var confirmation = await orders.PlaceOrderAsync(request);
                    return TypedResults.Created($"/api/orders/{confirmation.OrderId}", confirmation);
                }
                catch (InsufficientStockException e)
                {
                    return TypedResults.Problem(statusCode: StatusCodes.Status409Conflict, detail: e.Message);
                }
                catch (UnknownProductException e)
                {
                    return TypedResults.Problem(statusCode: StatusCodes.Status404NotFound, detail: e.Message);
                }
            })
            .WithName("PlaceOrder")
            .AddEndpointFilter<AuditFilter>();
        return app;
    }
}
