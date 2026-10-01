// VERDICT | T06 Extension methods | BETTER: C#
// WHY: extension methods/properties read left-to-right on the object; Java needs static Utils classes called inside-out.

namespace Shop.T06_Extensions;

public static class Demo
{
    public static void Run()
    {
        var orders = SampleData.Orders;

        // Reads left-to-right in the order the steps happen.
        var januaryBelgrade = orders.PlacedIn(2026, 1).From("Belgrade").Revenue;
        Console.WriteLine($"January revenue from Belgrade: {januaryBelgrade}");

        Console.WriteLine("Clean Code: A Handbook of Agile Craftsmanship".ToSlug());
        Console.WriteLine("Refactoring improves design".Truncate(11));
    }
}
