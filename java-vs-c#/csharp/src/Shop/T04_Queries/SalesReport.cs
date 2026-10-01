// VERDICT | T04 LINQ vs Streams | BETTER: C#
// WHY: GroupBy/Sum/MaxBy, tuples and query syntax; Java goes through Collectors and needs a record per intermediate shape.

namespace Shop.T04_Queries;

/// <summary>
/// LINQ: GroupBy/Sum/MaxBy directly on sequences, decimal has operators and tuples
/// replace the helper records. Query syntax reads like SQL. The same operators run
/// against a database through IQueryable + expression trees (e.g. EF Core).
/// </summary>
public static class SalesReport
{
    public static Dictionary<Category, decimal> RevenueByCategory(IEnumerable<Order> orders) =>
        orders.SelectMany(o => o.Lines)
              .GroupBy(l => l.Product.Category)
              .ToDictionary(g => g.Key, g => g.Sum(l => l.Total));

    public static List<(string Name, decimal Total)> TopCustomers(IEnumerable<Order> orders, int n) =>
        orders.GroupBy(o => o.Customer.Name)
              .Select(g => (Name: g.Key, Total: g.Sum(o => o.Total)))
              .OrderByDescending(x => x.Total)
              .Take(n)
              .ToList();

    public static (string Name, int Quantity) BestSeller(IEnumerable<Order> orders) =>
        orders.SelectMany(o => o.Lines)
              .GroupBy(l => l.Product.Name)
              .Select(g => (Name: g.Key, Quantity: g.Sum(l => l.Quantity)))
              .MaxBy(x => x.Quantity);

    public static List<(DateOnly Month, decimal Total)> MonthlySales(IEnumerable<Order> orders) =>
        orders.GroupBy(o => new DateOnly(o.Date.Year, o.Date.Month, 1))
              .Select(g => (Month: g.Key, Total: g.Sum(o => o.Total)))
              .OrderBy(x => x.Month)
              .ToList();

    /// <summary>Customers from a city who spent at least <paramref name="min"/>, biggest first.</summary>
    public static List<string> BigSpendersIn(IEnumerable<Order> orders, string city, decimal min) =>
        (from o in orders
         where o.Customer.City == city
         group o by o.Customer.Name into g
         let total = g.Sum(o => o.Total)
         where total >= min
         orderby total descending
         select $"{g.Key}: {total:F2}").ToList();
}
