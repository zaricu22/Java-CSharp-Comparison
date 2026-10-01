// VERDICT | T15 Enums | BETTER: JAVA
// WHY: enum constants carry fields, own method bodies and implement interfaces; C# enums are plain ints ((Enum)42 is legal).

namespace Shop.T15_Enums;

public static class Demo
{
    public static void Run()
    {
        var subtotal = 30.00m;
        Console.WriteLine($"Quotes for 30.00: {string.Join(", ", ShippingReport.Quotes(subtotal))}");
        Console.WriteLine($"{ShippingMethod.Express.Label} arrives {ShippingMethod.Express.Eta(new DateOnly(2026, 3, 1)):yyyy-MM-dd}");
        Console.WriteLine($"Parse(\"Pickup\") = {Enum.Parse<ShippingMethod>("Pickup").Label}");
        Console.WriteLine($"Total with standard: {ShippingReport.TotalWith(new ShippingRule(ShippingMethod.Standard), subtotal)}");
        Console.WriteLine($"Parse(\"42\") = {Enum.Parse<ShippingMethod>("42")} (defined? {Enum.IsDefined((ShippingMethod)42)})");
    }
}
