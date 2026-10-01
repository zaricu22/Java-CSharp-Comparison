// VERDICT | Domain model | BETTER: C#
// WHY: positional records with computed members fit in one file; Java needs one file per public type and BigDecimal methods instead of decimal operators.

namespace Shop.Domain;

/// <summary>The same data set exists on the Java side, so both test suites assert identical numbers.</summary>
public static class SampleData
{
    public static readonly Product CleanCode   = new("B1", "Clean Code",  Category.Books,       35.50m);
    public static readonly Product Refactoring = new("B2", "Refactoring", Category.Books,       42.00m);
    public static readonly Product Keyboard    = new("E1", "Keyboard",    Category.Electronics, 89.99m);
    public static readonly Product Monitor     = new("E2", "Monitor",     Category.Electronics, 249.00m);
    public static readonly Product Coffee      = new("G1", "Coffee",      Category.Grocery,     12.40m);

    public static readonly Customer Ana    = new(1, "Ana",    "Belgrade");
    public static readonly Customer Marko  = new(2, "Marko",  "Novi Sad");
    public static readonly Customer Jelena = new(3, "Jelena", "Belgrade");

    public static IReadOnlyList<Product> Products => [CleanCode, Refactoring, Keyboard, Monitor, Coffee];

    public static IReadOnlyList<Order> Orders =>
    [
        new(1, Ana,    new(2026, 1, 10), [new(CleanCode, 2), new(Keyboard, 1)]),
        new(2, Marko,  new(2026, 1, 15), [new(Monitor, 1), new(Coffee, 3)]),
        new(3, Ana,    new(2026, 2, 2),  [new(Coffee, 5)]),
        new(4, Jelena, new(2026, 2, 20), [new(Refactoring, 1), new(Keyboard, 2)]),
    ];
}
