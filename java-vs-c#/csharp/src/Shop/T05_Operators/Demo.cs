// VERDICT | T05 Operators & value types | BETTER: C#
// WHY: operator overloading, structs, decimal and unsigned types give `(price * qty + ship) * 0.9m`; Java chains methods and masks bytes.

namespace Shop.T05_Operators;

public static class Demo
{
    public static void Run()
    {
        var book = Money.Eur(35.50m);
        var shipping = Money.Eur(4.99m);
        Console.WriteLine($"Total: {Checkout.Total(book, 2, shipping, 0.9m)}");
        Console.WriteLine($"71.00 > 70? {book * 2 > Money.Eur(70)}");
        var prices = new Money[3];
        Console.WriteLine($"new Money[3], element 0 = {prices[0]}");
        Console.WriteLine($"Checksum: {PacketReader.Checksum([0xFF, 0x01])}");
    }
}
