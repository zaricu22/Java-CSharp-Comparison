// VERDICT | Domain model | BETTER: ASP.NET
// WHY: EF Core maps plain classes by convention (properties, no annotations); JPA needs @Entity/@Id, a protected no-arg constructor and getters.

namespace Shop.Api.Domain;

public enum Category { Books, Electronics, Grocery }

// Plain classes: no attributes, no parameterless-constructor ritual, properties instead of getters.
// Keys, relationships and column types come from conventions (+ a few lines in ShopDbContext).

public class Product
{
    public required string Sku { get; init; }
    public required string Name { get; set; }
    public Category Category { get; set; }
    public decimal Price { get; set; }
    public int Stock { get; set; }

    public void RemoveStock(int quantity)
    {
        if (quantity > Stock)
        {
            throw new InsufficientStockException(Sku, Stock, quantity);
        }
        Stock -= quantity;
    }

    public void AddStock(int quantity) => Stock += quantity;
}

public class Order
{
    public int Id { get; set; }
    public required string Customer { get; set; }
    public DateOnly PlacedOn { get; set; }
    public List<OrderLine> Lines { get; } = [];

    public void AddLine(Product product, int quantity) =>
        Lines.Add(new OrderLine { Product = product, ProductSku = product.Sku, Quantity = quantity, UnitPrice = product.Price });

    public decimal Total => Lines.Sum(l => l.UnitPrice * l.Quantity);
}

public class OrderLine
{
    public int Id { get; set; }
    public int OrderId { get; set; }
    public Order Order { get; set; } = null!;
    public required string ProductSku { get; set; }
    public Product Product { get; set; } = null!;
    public int Quantity { get; set; }
    public decimal UnitPrice { get; set; }
}

public class InsufficientStockException(string sku, int available, int requested)
    : Exception($"Only {available} of {sku} in stock, {requested} requested");
