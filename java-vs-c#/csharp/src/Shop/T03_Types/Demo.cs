// VERDICT | T03 Type system & generics | BETTER: C#
// WHY: reified generics (typeof(T), new T(), List<int> without boxing) and generic math; Java has erasure and Integer boxing traps.

namespace Shop.T03_Types;

public static class Demo
{
    public static void Run()
    {
        Console.WriteLine(TypeSystem.DescribeStock(255));
        Console.WriteLine($"Units: {TypeSystem.TotalUnits([2, 1, 3])}");
        Console.WriteLine($"Sum ints: {TypeSystem.Sum([1, 2, 3])}");
        Console.WriteLine($"Sum money: {TypeSystem.Sum([1.10m, 2.20m])}");

        var drafts = new Repository<CartDraft>();
        drafts.CreateNew();
        Console.WriteLine($"{drafts.EntityName} count={drafts.Count}");

        Console.WriteLine($"Empty List<int> is List<string>? {TypeSystem.IsListOfStrings(new List<int>())}");
    }
}
