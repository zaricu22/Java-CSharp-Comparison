// VERDICT | T14 Checked exceptions | BETTER: JAVA
// WHY: the compiler forces callers to handle declared failures; in C# a forgotten catch compiles and fails at runtime.

namespace Shop.T14_Checked;

public static class Demo
{
    public static void Run()
    {
        var service = new CheckoutService(new FakeCardProcessor());
        Console.WriteLine(service.Checkout("4111-1111", 99.00m));
        Console.WriteLine(service.Checkout("0000-1111", 99.00m));
        Console.WriteLine($"[{string.Join(", ", service.CheckoutAll("4111-1111", [10m, 5000m]))}]");
        try
        {
            service.CheckoutForgettingErrors("0000-1111", 1m);
        }
        catch (PaymentDeclinedException e)
        {
            Console.WriteLine($"Unhandled at compile time, crashed at runtime: {e.Message}");
        }
    }
}
