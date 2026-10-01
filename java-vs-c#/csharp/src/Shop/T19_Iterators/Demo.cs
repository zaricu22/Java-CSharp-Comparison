// VERDICT | T19 Iterators & generators | BETTER: C#
// WHY: yield return and async streams (await foreach) write the state machine for you; Java needs a hand-written Iterator.

namespace Shop.T19_Iterators;

public static class Demo
{
    public static async Task RunAsync()
    {
        var days = Iterators.DeliveryDays(new DateOnly(2026, 1, 9)).Take(3).Select(d => d.ToString("yyyy-MM-dd"));
        Console.WriteLine($"Next 3 delivery days after Fri 2026-01-09: [{string.Join(", ", days)}]");

        var api = new OrderPages(SampleData.Orders);
        var ids = Iterators.AllOrders(api, 3).Select(o => o.Id).ToList();
        Console.WriteLine($"All order ids: [{string.Join(", ", ids)}] (pages fetched: {api.PagesFetched})");

        var lazyApi = new OrderPages(SampleData.Orders);
        var firstTwo = Iterators.AllOrders(lazyApi, 3).Take(2).Select(o => o.Id).ToList();
        Console.WriteLine($"First two: [{string.Join(", ", firstTwo)}] (pages fetched: {lazyApi.PagesFetched})");

        var asyncApi = new OrderPages(SampleData.Orders);
        var total = 0m;
        await foreach (var order in Iterators.AllOrdersAsync(asyncApi, 3))
        {
            total += order.Total;
        }
        Console.WriteLine($"await foreach total: {total}");
    }
}
