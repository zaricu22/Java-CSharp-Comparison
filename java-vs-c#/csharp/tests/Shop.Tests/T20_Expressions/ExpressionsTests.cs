// VERDICT | T20 Expression trees | BETTER: C#
// WHY: a plain lambda can be inspected and translated to SQL (EF Core); Java lambdas are opaque, so you need a Criteria/metamodel DSL.

namespace Shop.T20_Expressions;

public class ExpressionsTests
{
    [Fact]
    public void LambdaTranslatesToSql()
    {
        var minPrice = 40m; // captured variables become parameters/literals
        Assert.Equal("SELECT * FROM Products WHERE ((Price > 40) AND Name LIKE 'Key%')",
            SqlTranslator.Where<Product>(p => p.Price > minPrice && p.Name.StartsWith("Key")));
        Assert.Equal("SELECT * FROM Customers WHERE ((City = 'Belgrade') OR (Id <= 2))",
            SqlTranslator.Where<Customer>(c => c.City == "Belgrade" || c.Id <= 2));
    }

    [Fact]
    public void SameLambdaFiltersInMemory()
    {
        Assert.Equal(["Keyboard"],
            SqlTranslator.Filter(SampleData.Products, p => p.Price > 40m && p.Name.StartsWith("Key")).Select(p => p.Name));
    }

    [Fact]
    public void StringLiteralsAreEscaped()
    {
        Assert.Equal("SELECT * FROM Customers WHERE (City = 'O''Hara')", SqlTranslator.Where<Customer>(c => c.City == "O'Hara"));
    }

    [Fact]
    public void UnsupportedExpressionsFailLoudly()
    {
        // Like EF Core: anything the translator cannot map to SQL is rejected, not silently ignored
        Assert.Throws<NotSupportedException>(() => SqlTranslator.Where<Product>(p => p.Name.Length > 3 && p.Name.EndsWith("d")));
    }
}
