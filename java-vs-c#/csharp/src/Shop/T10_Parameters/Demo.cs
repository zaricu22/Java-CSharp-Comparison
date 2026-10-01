// VERDICT | T10 Method parameters | BETTER: C#
// WHY: optional/named arguments, out/ref and tuple returns; Java needs parameter objects, result records and try/catch parsing.

namespace Shop.T10_Parameters;

public static class Demo
{
    public static void Run()
    {
        Console.WriteLine(Parameters.Quote(100m));
        Console.WriteLine(Parameters.Quote(100m, express: true));
        Console.WriteLine(Parameters.Quote(100m, discountPercent: 10, currency: "USD"));

        var (min, max) = Parameters.PriceRange(SampleData.Products);
        Console.WriteLine($"Prices from {min} to {max}");

        Console.WriteLine(Parameters.TryParseQuantity("3", out var quantity)
            ? $"Parsed quantity {quantity}"
            : "Invalid quantity");
    }
}
