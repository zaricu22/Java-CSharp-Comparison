// VERDICT | T04 LINQ vs Streams | BETTER: C#
// WHY: GroupBy/Sum/MaxBy, tuples and query syntax; Java goes through Collectors and needs a record per intermediate shape.

namespace Shop.T04_Queries;

public class SalesReportTests
{
    private readonly IReadOnlyList<Order> _orders = SampleData.Orders;

    [Fact]
    public void RevenueByCategory()
    {
        Assert.Equal(new Dictionary<Category, decimal>
        {
            [Category.Books] = 113.00m,
            [Category.Electronics] = 518.97m,
            [Category.Grocery] = 99.20m,
        }, SalesReport.RevenueByCategory(_orders));
    }

    [Fact]
    public void TopCustomers()
    {
        Assert.Equal([("Marko", 286.20m), ("Ana", 222.99m)], SalesReport.TopCustomers(_orders, 2));
    }

    [Fact]
    public void BestSeller()
    {
        var (name, quantity) = SalesReport.BestSeller(_orders);
        Assert.Equal("Coffee", name);
        Assert.Equal(8, quantity);
    }

    [Fact]
    public void MonthlySales()
    {
        Assert.Equal([(new DateOnly(2026, 1, 1), 447.19m), (new DateOnly(2026, 2, 1), 283.98m)],
            SalesReport.MonthlySales(_orders));
    }

    [Fact]
    public void BigSpendersInCity()
    {
        Assert.Equal(["Ana: 222.99", "Jelena: 221.98"], SalesReport.BigSpendersIn(_orders, "Belgrade", 200m));
        Assert.Equal(["Ana: 222.99"], SalesReport.BigSpendersIn(_orders, "Belgrade", 222m));
    }
}
