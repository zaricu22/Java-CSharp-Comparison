// VERDICT | T02 Strings | BETTER: C#
// WHY: interpolation puts each value where it is printed ({x,-12} {y:F2}); Java only has positional %s placeholders.

namespace Shop.T02_Strings;

public static class Demo
{
    public static void Run()
    {
        var order = SampleData.Orders[0];
        Console.WriteLine(InvoiceFormatter.Greeting("Ana", 2));
        Console.WriteLine(InvoiceFormatter.Invoice(order));
        Console.WriteLine(InvoiceFormatter.ToJson(order));
    }
}
