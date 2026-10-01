// VERDICT | T23 Collection expressions | BETTER: C#
// WHY: one [a, ..b, ..c] syntax builds arrays, lists, sets and immutable collections; Java mixes List.of, addAll and streams, and List.of hides immutability until add() throws.

namespace Shop.T23_Collections;

public static class Demo
{
    public static void Run()
    {
        var featured = CollectionLiterals.Featured(SampleData.Monitor, [SampleData.Coffee, SampleData.CleanCode], [SampleData.Refactoring]);
        Console.WriteLine($"Featured: [{string.Join(", ", featured.Select(p => p.Name))}]");
        Console.WriteLine($"Merged quantities: [{string.Join(", ", CollectionLiterals.MergeQuantities([2, 1], [3, 5]))}]");

        var tags = CollectionLiterals.DefaultTags();
        tags.Add("bestseller");
        Console.WriteLine($"Tags: [{string.Join(", ", tags)}]");
        Console.WriteLine($"Ships to DE? {CollectionLiterals.ShippingCountries.Contains("DE")}");
    }
}
