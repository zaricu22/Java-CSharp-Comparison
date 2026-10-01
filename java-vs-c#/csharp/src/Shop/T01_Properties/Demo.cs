// VERDICT | T01 Properties & records | BETTER: C#
// WHY: properties (`field`, `required`, `init`) and `with` replace Java's hand-written getters/setters and manual record copies.

namespace Shop.T01_Properties;

public static class Demo
{
    public static void Run()
    {
        // Object initializer: named, any order, and "required" makes the compiler check nothing is missing
        var ana = new CustomerAccount { Id = 1, Email = "ana@shop.rs", FirstName = "Ana", LastName = "Jovanovic" };
        ana.LastName = "Petrovic";
        ana.AddPoints(150);
        Console.WriteLine($"{ana.FullName} <{ana.Email}> points={ana.LoyaltyPoints}");

        Console.WriteLine($"Discounted: {Pricing.Discounted(SampleData.CleanCode, 10)}");
    }
}
