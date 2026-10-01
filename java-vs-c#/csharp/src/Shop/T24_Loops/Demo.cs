// VERDICT | T24 Labeled break & continue | BETTER: JAVA
// WHY: `break search;` / `continue orders;` leave or skip an outer loop directly; C# needs goto, a flag variable or an extra method.

namespace Shop.T24_Loops;

public static class Demo
{
    public static void Run()
    {
        var orders = SampleData.Orders;
        HashSet<string> outOfStock = ["E1"]; // Keyboard
        Console.WriteLine(LoopControl.FirstUnavailable(orders, outOfStock));
        Console.WriteLine($"Shippable orders: [{string.Join(", ", LoopControl.ShippableOrders(orders, outOfStock))}]");
    }
}
