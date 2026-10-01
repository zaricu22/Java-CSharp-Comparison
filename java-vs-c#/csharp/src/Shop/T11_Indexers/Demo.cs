// VERDICT | T11 Indexers & ranges | BETTER: C#
// WHY: catalog["E1"], matrix[1, 1], items[1..^1]; Java spells everything as get()/subList()/substring().

namespace Shop.T11_Indexers;

public static class Demo
{
    public static void Run()
    {
        var catalog = new Catalog(SampleData.Products);
        Console.WriteLine($"catalog[\"E1\"] = {catalog["E1"].Name}");
        Console.WriteLine($"Books: [{string.Join(", ", catalog[Category.Books].Select(p => p.Name))}]");

        var matrix = new ShippingMatrix();
        Console.WriteLine($"EU, medium parcel: {matrix[1, 1]}");

        string[] skus = ["B1", "B2", "E1", "E2", "G1"];
        Console.WriteLine($"Last two: [{string.Join(", ", Ranges.LastN(skus, 2))}], middle: [{string.Join(", ", Ranges.WithoutFirstAndLast(skus))}]");
    }
}
