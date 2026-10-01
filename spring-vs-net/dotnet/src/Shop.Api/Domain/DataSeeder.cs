// VERDICT | Domain model | BETTER: ASP.NET
// WHY: EF Core maps plain classes by convention (properties, no annotations); JPA needs @Entity/@Id, a protected no-arg constructor and getters.

using Microsoft.EntityFrameworkCore;

namespace Shop.Api.Domain;

/// <summary>Same seed data as the Spring side, so both test suites assert identical numbers.</summary>
public static class DataSeeder
{
    public static async Task SeedAsync(this WebApplication app)
    {
        using var scope = app.Services.CreateScope();
        var db = scope.ServiceProvider.GetRequiredService<ShopDbContext>();
        await db.Database.EnsureCreatedAsync(); // a real app would use EF migrations

        if (await db.Products.AnyAsync())
        {
            return;
        }

        var cleanCode = new Product { Sku = "B1", Name = "Clean Code", Category = Category.Books, Price = 35.50m, Stock = 12 };
        var refactoring = new Product { Sku = "B2", Name = "Refactoring", Category = Category.Books, Price = 42.00m, Stock = 7 };
        var keyboard = new Product { Sku = "E1", Name = "Keyboard", Category = Category.Electronics, Price = 89.99m, Stock = 3 };
        var monitor = new Product { Sku = "E2", Name = "Monitor", Category = Category.Electronics, Price = 249.00m, Stock = 0 };
        var coffee = new Product { Sku = "G1", Name = "Coffee", Category = Category.Grocery, Price = 12.40m, Stock = 40 };
        db.Products.AddRange(cleanCode, refactoring, keyboard, monitor, coffee);
        await db.SaveChangesAsync();

        // One save per order keeps the ids 1..4 in this order, like the Spring side
        await AddOrderAsync(db, "Ana", new DateOnly(2026, 1, 10), (cleanCode, 2), (keyboard, 1));
        await AddOrderAsync(db, "Marko", new DateOnly(2026, 1, 15), (monitor, 1), (coffee, 3));
        await AddOrderAsync(db, "Ana", new DateOnly(2026, 2, 2), (coffee, 5));
        await AddOrderAsync(db, "Jelena", new DateOnly(2026, 2, 20), (refactoring, 1), (keyboard, 2));
    }

    private static async Task AddOrderAsync(ShopDbContext db, string customer, DateOnly date, params (Product Product, int Quantity)[] lines)
    {
        var order = new Order { Customer = customer, PlacedOn = date };
        foreach (var (product, quantity) in lines)
        {
            order.AddLine(product, quantity);
        }
        db.Orders.Add(order);
        await db.SaveChangesAsync();
    }
}
