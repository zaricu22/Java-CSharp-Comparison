// VERDICT | T06 Complex queries | BETTER: ASP.NET
// WHY: LINQ is type-checked and refactor-safe (GroupBy/Sum/conditional Where); Spring uses JPQL strings or the verbose Criteria/Specification API with string attribute names.

using Microsoft.EntityFrameworkCore;

namespace Shop.Api.T06_Queries;

public record CategoryRevenue(Category Category, decimal Revenue);

public record CustomerTotal(string Customer, decimal Total);

/// <summary>
/// The same queries as the Spring JPQL strings and Criteria API - as ordinary, compiler-checked C#.
/// EF Core translates them to SQL (GROUP BY, SUM, EXISTS). Renaming a property updates every query.
/// </summary>
public static class OrderReports
{
    public static Task<List<CategoryRevenue>> RevenueByCategoryAsync(this ShopDbContext db) =>
        db.OrderLines
            .GroupBy(l => l.Product.Category)
            .OrderByDescending(g => g.Sum(l => l.UnitPrice * l.Quantity))
            .Select(g => new CategoryRevenue(g.Key, g.Sum(l => l.UnitPrice * l.Quantity)))
            .ToListAsync();

    public static Task<List<CustomerTotal>> TopCustomersSinceAsync(this ShopDbContext db, DateOnly from, int limit) =>
        db.OrderLines
            .Where(l => l.Order.PlacedOn >= from)
            .GroupBy(l => l.Order.Customer)
            .OrderByDescending(g => g.Sum(l => l.UnitPrice * l.Quantity))
            .Take(limit)
            .Select(g => new CustomerTotal(g.Key, g.Sum(l => l.UnitPrice * l.Quantity)))
            .ToListAsync();

    /// <summary>Optional filters are just "if" + Where - no Criteria API, no attribute-name strings.</summary>
    public static Task<List<int>> SearchOrderIdsAsync(this ShopDbContext db, string? customer, Category? category)
    {
        IQueryable<Order> orders = db.Orders;
        if (customer is not null)
        {
            orders = orders.Where(o => o.Customer == customer);
        }
        if (category is not null)
        {
            orders = orders.Where(o => o.Lines.Any(l => l.Product.Category == category));
        }
        return orders.OrderBy(o => o.Id).Select(o => o.Id).ToListAsync();
    }

    public static IEndpointRouteBuilder MapReportEndpoints(this IEndpointRouteBuilder app)
    {
        var reports = app.MapGroup("/api/reports");
        reports.MapGet("/revenue-by-category", (ShopDbContext db) => db.RevenueByCategoryAsync());
        reports.MapGet("/top-customers", (DateOnly since, ShopDbContext db, int limit = 3) => db.TopCustomersSinceAsync(since, limit));
        reports.MapGet("/orders", (string? customer, Category? category, ShopDbContext db) => db.SearchOrderIdsAsync(customer, category));
        return app;
    }
}
