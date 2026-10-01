// VERDICT | T10 Method parameters | BETTER: C#
// WHY: optional/named arguments, out/ref and tuple returns; Java needs parameter objects, result records and try/catch parsing.

namespace Shop.T10_Parameters;

public static class Parameters
{
    private const decimal ExpressFee = 9.99m;

    /// <summary>Optional parameters with defaults; callers name only what they change.</summary>
    public static string Quote(decimal subtotal, decimal discountPercent = 0, bool express = false, string currency = "EUR")
    {
        var total = Math.Round(subtotal * (100 - discountPercent) / 100, 2);
        if (express)
        {
            total += ExpressFee;
        }
        return $"{total:F2} {currency}";
    }

    /// <summary>Multiple return values as a named tuple - no extra type needed.</summary>
    public static (decimal Min, decimal Max) PriceRange(IEnumerable<Product> products) =>
        (products.Min(p => p.Price), products.Max(p => p.Price));

    /// <summary>The TryParse pattern: "out" returns the value, the bool says whether it worked.</summary>
    public static bool TryParseQuantity(string input, out int quantity) =>
        int.TryParse(input, out quantity) && quantity > 0;

    /// <summary>"ref" lets a method change the caller's variables (plus tuple swap).</summary>
    public static void Normalize(ref decimal min, ref decimal max)
    {
        if (min > max)
        {
            (min, max) = (max, min);
        }
    }
}
