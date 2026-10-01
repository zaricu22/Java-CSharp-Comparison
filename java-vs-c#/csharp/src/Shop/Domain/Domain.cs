// VERDICT | Domain model | BETTER: C#
// WHY: positional records with computed members fit in one file; Java needs one file per public type and BigDecimal methods instead of decimal operators.

namespace Shop.Domain;

// The whole Java "domain" package (5 files) fits in one C# file.

public enum Category { Books, Electronics, Grocery }

public record Product(string Sku, string Name, Category Category, decimal Price);

public record Customer(int Id, string Name, string City);

public record OrderLine(Product Product, int Quantity)
{
    public decimal Total => Product.Price * Quantity;
}

public record Order(int Id, Customer Customer, DateOnly Date, IReadOnlyList<OrderLine> Lines)
{
    public decimal Total => Lines.Sum(l => l.Total);
}
