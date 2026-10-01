// VERDICT | T04 LINQ vs Streams | BETTER: C#
// WHY: GroupBy/Sum/MaxBy, tuples and query syntax; Java goes through Collectors and needs a record per intermediate shape.

namespace Shop.T04_Queries;

public static class Demo
{
    public static void Run()
    {
        var orders = SampleData.Orders;
        Console.WriteLine($"By category:  {string.Join(", ", SalesReport.RevenueByCategory(orders))}");
        Console.WriteLine($"Top 2:        {string.Join(", ", SalesReport.TopCustomers(orders, 2))}");
        Console.WriteLine($"Best seller:  {SalesReport.BestSeller(orders)}");
        Console.WriteLine($"Monthly:      {string.Join(", ", SalesReport.MonthlySales(orders).Select(m => $"{m.Month:yyyy-MM}={m.Total}"))}");
        Console.WriteLine($"Big spenders: {string.Join(", ", SalesReport.BigSpendersIn(orders, "Belgrade", 200m))}");
    }
}
