// VERDICT | T20 Expression trees | BETTER: C#
// WHY: a plain lambda can be inspected and translated to SQL (EF Core); Java lambdas are opaque, so you need a Criteria/metamodel DSL.

namespace Shop.T20_Expressions;

public static class Demo
{
    public static void Run()
    {
        var minPrice = 40m;
        // One plain lambda, used twice: as SQL and as an in-memory filter
        Console.WriteLine(SqlTranslator.Where<Product>(p => p.Price > minPrice && p.Name.StartsWith("Key")));
        var matches = SqlTranslator.Filter(SampleData.Products, p => p.Price > minPrice && p.Name.StartsWith("Key"));
        Console.WriteLine($"In memory: [{string.Join(", ", matches.Select(p => p.Name))}]");

        Console.WriteLine(SqlTranslator.Where<Customer>(c => c.City == "Belgrade" || c.Id <= 2));
    }
}
