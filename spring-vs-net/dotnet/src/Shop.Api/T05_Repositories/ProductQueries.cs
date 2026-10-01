// VERDICT | T05 Repositories | BETTER: SPRING
// WHY: Spring Data derives queries from method names with zero implementation; EF's DbSet is already a repository, but every query is written as LINQ.

using Microsoft.EntityFrameworkCore;

namespace Shop.Api.T05_Repositories;

/// <summary>
/// DbSet&lt;Product&gt; already is the generic repository (Find, Add, Remove, Count...).
/// Named queries are written by hand - here as reusable IQueryable extensions, the idiomatic
/// EF alternative to a repository class. Compare with the Spring interface that has no bodies at all.
/// </summary>
public static class ProductQueries
{
    public static IQueryable<Product> InCategory(this IQueryable<Product> products, Category category) =>
        products.Where(p => p.Category == category).OrderBy(p => p.Price);

    public static IQueryable<Product> Affordable(this IQueryable<Product> products, decimal maxPrice, int minStock = 0) =>
        products.Where(p => p.Price < maxPrice && p.Stock > minStock).OrderBy(p => p.Price);

    public static IQueryable<Product> NameContains(this IQueryable<Product> products, string text) =>
        products.Where(p => EF.Functions.Like(p.Name, $"%{text}%")).OrderBy(p => p.Name);

    public static Task<int> CountLowStockAsync(this IQueryable<Product> products, int threshold) =>
        products.CountAsync(p => p.Stock < threshold);
}

public static class InventoryEndpoints
{
    public static IEndpointRouteBuilder MapInventoryEndpoints(this IEndpointRouteBuilder app)
    {
        app.MapGet("/api/inventory/affordable", async (decimal maxPrice, ShopDbContext db) =>
            (await db.Products.Affordable(maxPrice).AsNoTracking().ToListAsync()).Select(T01_Endpoints.ProductDto.From));

        app.MapGet("/api/inventory/low-stock-count", async (ShopDbContext db, int threshold = 5) =>
            new { count = await db.Products.CountLowStockAsync(threshold) });

        return app;
    }
}
