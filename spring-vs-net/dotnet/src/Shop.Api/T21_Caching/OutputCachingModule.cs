// VERDICT | T21 Output caching | BETTER: ASP.NET
// WHY: OutputCache stores whole HTTP responses (vary by query, tag eviction) without touching service code; Spring has only method-level @Cacheable - the controller and JSON serialization still run.

using Microsoft.AspNetCore.OutputCaching;
using Shop.Api.T01_Endpoints;

namespace Shop.Api.T21_Caching;

/// <summary>Counts how often the endpoint really executes (to show cache hits in the tests).</summary>
public sealed class CatalogQueryCounter
{
    private int _count;

    public int Count => Volatile.Read(ref _count);

    public void Increment() => Interlocked.Increment(ref _count);
}

/// <summary>
/// The whole HTTP response is cached by the middleware: on a hit the endpoint, the database and JSON
/// serialization are all skipped. The cache varies by query string and is evicted by tag - the
/// endpoint code itself does not know about caching (Redis-backed stores plug in the same way).
/// </summary>
public static class OutputCachingModule
{
    public const string CatalogPolicy = "catalog";
    public const string ProductsTag = "products";

    public static IServiceCollection AddShopOutputCaching(this IServiceCollection services)
    {
        services.AddSingleton<CatalogQueryCounter>();
        services.AddOutputCache(cache => cache.AddPolicy(CatalogPolicy, policy => policy
            .Expire(TimeSpan.FromMinutes(5))
            .SetVaryByQuery("category")
            .Tag(ProductsTag)));
        return services;
    }

    public static IEndpointRouteBuilder MapCachedCatalog(this IEndpointRouteBuilder app)
    {
        app.MapGet("/api/cached/products", async (Category? category, ICatalogService catalog, CatalogQueryCounter counter) =>
            {
                counter.Increment();
                return await catalog.ListAsync(category);
            })
            .CacheOutput(CatalogPolicy);

        app.MapPut("/api/cached/products/{sku}/price", async (string sku, decimal price, ShopDbContext db, IOutputCacheStore cache, CancellationToken ct) =>
        {
            if (await db.Products.FindAsync([sku], ct) is not { } product)
            {
                return Results.NotFound();
            }
            product.Price = price;
            await db.SaveChangesAsync(ct);
            await cache.EvictByTagAsync(ProductsTag, ct);
            return Results.NoContent();
        });

        return app;
    }
}
