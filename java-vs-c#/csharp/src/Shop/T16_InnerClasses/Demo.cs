// VERDICT | T16 Anonymous & inner classes | BETTER: JAVA
// WHY: anonymous classes implement multi-method interfaces inline and inner classes see the outer instance; C# needs named classes.

namespace Shop.T16_InnerClasses;

public static class Demo
{
    public static void Run()
    {
        var cart = new ShoppingCart();
        var books = cart.Add(SampleData.CleanCode, 2);
        var keyboard = cart.Add(SampleData.Keyboard, 1);
        Console.WriteLine($"Subtotal {cart.Subtotal}, keyboard share {keyboard.ShareOfCart}%, books {books.ShareOfCart}%");

        var rule = Discounts.OneTimePercent(100m, 10);
        Console.WriteLine($"{rule.Name}: {rule.Apply(cart.Subtotal)} then {rule.Apply(cart.Subtotal)}");

        keyboard.Remove();
        Console.WriteLine($"After removing keyboard: {cart.Count} item(s), subtotal {cart.Subtotal}");
    }
}
