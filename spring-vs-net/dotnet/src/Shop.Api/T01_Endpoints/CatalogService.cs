// VERDICT | T01 Endpoints & hosting | BETTER: ASP.NET
// WHY: minimal APIs map a route to a lambda with typed results in one line; Spring needs a @RestController class (Java 21 sealed switch maps results as cleanly as C#).

using Microsoft.EntityFrameworkCore;
using Shop.Api.T04_Validation;
using Shop.Api.T05_Repositories;

namespace Shop.Api.T01_Endpoints;

public record ProductDto(string Sku, string Name, Category Category, decimal Price, int Stock)
{
    public static ProductDto From(Product p) => new(p.Sku, p.Name, p.Category, p.Price, p.Stock);
}

/// <summary>Outcome of registering a product; the endpoint turns each case into an HTTP result.</summary>
public abstract record RegisterResult
{
    private RegisterResult() { }

    public sealed record Created(ProductDto Product) : RegisterResult;

    public sealed record DuplicateSku(string Sku) : RegisterResult;
}

/// <summary>
/// An interface only because .NET fakes/mocks need one (T15); Mockito on the Spring side
/// mocks the concrete CatalogService class directly.
/// </summary>
public interface ICatalogService
{
    Task<List<ProductDto>> ListAsync(Category? category);
    Task<ProductDto?> FindAsync(string sku);
    Task<RegisterResult> RegisterAsync(NewProduct request);
    Task<List<ProductDto>> SearchAsync(string text);
}

public sealed class CatalogService(ShopDbContext db) : ICatalogService
{
    public async Task<List<ProductDto>> ListAsync(Category? category)
    {
        var query = category is null ? db.Products.OrderBy(p => p.Sku) : db.Products.InCategory(category.Value);
        return (await query.AsNoTracking().ToListAsync()).Select(ProductDto.From).ToList();
    }

    public async Task<ProductDto?> FindAsync(string sku) =>
        await db.Products.FindAsync(sku) is { } product ? ProductDto.From(product) : null;

    public async Task<RegisterResult> RegisterAsync(NewProduct request)
    {
        if (await db.Products.AnyAsync(p => p.Sku == request.Sku))
        {
            return new RegisterResult.DuplicateSku(request.Sku);
        }
        var product = new Product
        {
            Sku = request.Sku, Name = request.Name, Category = request.Category!.Value, Price = request.Price, Stock = request.Stock,
        };
        db.Products.Add(product);
        await db.SaveChangesAsync();
        return new RegisterResult.Created(ProductDto.From(product));
    }

    public async Task<List<ProductDto>> SearchAsync(string text) =>
        (await db.Products.NameContains(text).AsNoTracking().ToListAsync()).Select(ProductDto.From).ToList();
}
