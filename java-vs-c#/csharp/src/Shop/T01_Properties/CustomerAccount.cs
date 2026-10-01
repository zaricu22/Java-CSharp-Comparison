// VERDICT | T01 Properties & records | BETTER: C#
// WHY: properties (`field`, `required`, `init`) and `with` replace Java's hand-written getters/setters and manual record copies.

namespace Shop.T01_Properties;

/// <summary>
/// Same entity as the Java version: read-only id, validated email, plain names,
/// points only the class may change, and a computed full name. No methods needed.
/// </summary>
public class CustomerAccount
{
    public required int Id { get; init; }

    // C# 14 "field" keyword: validation without declaring a backing field by hand
    public required string Email
    {
        get;
        set => field = value.Contains('@') ? value : throw new ArgumentException($"Invalid email: {value}");
    }

    public required string FirstName { get; set; }

    public required string LastName { get; set; }

    public int LoyaltyPoints { get; private set; }

    public string FullName => $"{FirstName} {LastName}";

    public void AddPoints(int points) => LoyaltyPoints += points;
}

public static class Pricing
{
    /// <summary>"with" copies a record and changes only the named members.</summary>
    public static Product Discounted(Product p, int percent) =>
        p with { Price = Math.Round(p.Price * (100 - percent) / 100, 2) };
}
