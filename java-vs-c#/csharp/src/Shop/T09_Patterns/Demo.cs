// VERDICT | T09 Pattern matching | BETTER: TIE
// WHY: C# has relational, tuple and list patterns; Java checks switch exhaustiveness over sealed types at compile time.

namespace Shop.T09_Patterns;

public static class Demo
{
    public static void Run()
    {
        PaymentResult[] results =
        [
            new PaymentResult.Approved("TX-1", 1500m),
            new PaymentResult.Declined("insufficient funds"),
            new PaymentResult.Pending(TimeSpan.FromSeconds(30)),
            new PaymentResult.FraudSuspected(95),
        ];
        foreach (var r in results)
        {
            Console.WriteLine(Patterns.Describe(r));
        }

        Console.WriteLine($"Shipping RS 3kg: {Patterns.ShippingCost("RS", 3)}");
        Console.WriteLine(Patterns.CartSummary(["Clean Code", "Keyboard", "Coffee"]));
    }
}
