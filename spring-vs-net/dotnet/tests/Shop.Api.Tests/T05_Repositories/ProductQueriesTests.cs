// VERDICT | T05 Repositories | BETTER: SPRING
// WHY: Spring Data derives queries from method names with zero implementation; EF's DbSet is already a repository, but every query is written as LINQ.

using Microsoft.EntityFrameworkCore;
using Shop.Api.T01_Endpoints;
using Shop.Api.T05_Repositories;

namespace Shop.Api.Tests.T05_Repositories;

/// <summary>No @DataJpaTest-style slice: the DbContext is taken from the full app's container.</summary>
public class ProductQueriesTests
{
    private static (ShopApiFactory Factory, IServiceScope Scope, ShopDbContext Db) Database()
    {
        var factory = new ShopApiFactory();
        factory.CreateClient(); // starts the app, which seeds the database
        var scope = factory.Services.CreateScope();
        return (factory, scope, scope.ServiceProvider.GetRequiredService<ShopDbContext>());
    }

    [Fact]
    public async Task CrudMethodsComeFromDbSet()
    {
        var (_, scope, db) = Database();
        using var _scope = scope;

        Assert.Equal(5, await db.Products.CountAsync());
        Assert.Equal("Keyboard", (await db.Products.FindAsync("E1"))!.Name);
        db.Products.Remove((await db.Products.FindAsync("E2"))!);
        await db.SaveChangesAsync();
        Assert.False(await db.Products.AnyAsync(p => p.Sku == "E2"));
    }

    [Fact]
    public async Task NamedQueriesAreHandWrittenLinq()
    {
        var (_, scope, db) = Database();
        using var _scope = scope;

        Assert.Equal(["B1", "B2"], await db.Products.InCategory(Category.Books).Select(p => p.Sku).ToListAsync());
        Assert.Equal(["G1", "B1", "B2"], await db.Products.Affordable(50m).Select(p => p.Sku).ToListAsync());
        Assert.Equal(["Clean Code", "Coffee", "Keyboard", "Monitor", "Refactoring"],
            await db.Products.NameContains("o").Select(p => p.Name).ToListAsync());
        Assert.Equal(2, await db.Products.CountLowStockAsync(5));
    }

    [Fact]
    public async Task InventoryEndpoints()
    {
        var client = new ShopApiFactory().CreateClient();
        var affordable = await client.GetFromJsonAsync<List<ProductDto>>("/api/inventory/affordable?maxPrice=50", ShopApiFactory.Json);
        Assert.Equal(["G1", "B1", "B2"], affordable!.Select(p => p.Sku));
        Assert.Equal(2, (await client.GetJsonAsync("/api/inventory/low-stock-count?threshold=5")).GetProperty("count").GetInt32());
    }
}
