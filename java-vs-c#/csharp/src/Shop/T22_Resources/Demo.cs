// VERDICT | T22 Resource cleanup | BETTER: TIE
// WHY: C# `using var` needs no nesting; Java try-with-resources keeps the original exception and records close() failures as suppressed.

namespace Shop.T22_Resources;

public static class Demo
{
    public static void Run()
    {
        var journal = new List<string>();
        Exporter.ExportOrders(journal, "1,Ana", false);
        Console.WriteLine($"Journal: [{string.Join(", ", journal)}]");

        try
        {
            Exporter.ExportOrders([], "", true); // body AND dispose fail
        }
        catch (InvalidOperationException e)
        {
            Console.WriteLine($"Caught: {e.Message} (the original 'write failed' is lost)");
        }
    }
}
