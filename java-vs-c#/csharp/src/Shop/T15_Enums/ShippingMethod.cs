// VERDICT | T15 Enums | BETTER: JAVA
// WHY: enum constants carry fields, own method bodies and implement interfaces; C# enums are plain ints ((Enum)42 is legal).

namespace Shop.T15_Enums;

/// <summary>A C# enum is just a named integer: no fields, no per-member behaviour, no interfaces.</summary>
public enum ShippingMethod { Standard, Express, Pickup }

public interface IPricingRule
{
    decimal Cost(decimal subtotal);
}

/// <summary>
/// The best modern C# can do: extension members (C# 14) attach data and behaviour from outside.
/// Each fact about a method lives in a different switch, and every switch needs a "_" arm,
/// because (ShippingMethod)42 is a perfectly legal value.
/// </summary>
public static class ShippingMethodExtensions
{
    private const decimal FreeShippingOver = 50m;

    extension(ShippingMethod method)
    {
        public string Label => method switch
        {
            ShippingMethod.Standard => "Standard delivery",
            ShippingMethod.Express => "Express delivery",
            ShippingMethod.Pickup => "Store pickup",
            _ => throw new ArgumentOutOfRangeException(nameof(method), method, "Unknown shipping method"),
        };

        public int Days => method switch
        {
            ShippingMethod.Standard => 5,
            ShippingMethod.Express => 1,
            ShippingMethod.Pickup => 0,
            _ => throw new ArgumentOutOfRangeException(nameof(method), method, "Unknown shipping method"),
        };

        public decimal Cost(decimal subtotal) => method switch
        {
            ShippingMethod.Standard => subtotal >= FreeShippingOver ? 0m : 4.99m,
            ShippingMethod.Express => 9.99m,
            ShippingMethod.Pickup => 0m,
            _ => throw new ArgumentOutOfRangeException(nameof(method), method, "Unknown shipping method"),
        };

        public DateOnly Eta(DateOnly orderDate) => orderDate.AddDays(method.Days);
    }
}

/// <summary>An enum cannot implement an interface, so polymorphic use needs an adapter.</summary>
public sealed class ShippingRule(ShippingMethod method) : IPricingRule
{
    public decimal Cost(decimal subtotal) => method.Cost(subtotal);
}

public static class ShippingReport
{
    public static readonly IReadOnlySet<ShippingMethod> HomeDelivery =
        new HashSet<ShippingMethod> { ShippingMethod.Standard, ShippingMethod.Express };

    public static Dictionary<ShippingMethod, decimal> Quotes(decimal subtotal) =>
        Enum.GetValues<ShippingMethod>().ToDictionary(m => m, m => m.Cost(subtotal));

    public static decimal TotalWith(IPricingRule rule, decimal subtotal) => subtotal + rule.Cost(subtotal);
}
