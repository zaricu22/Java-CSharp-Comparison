// VERDICT | T19 Iterators & generators | BETTER: C#
// WHY: yield return and async streams (await foreach) write the state machine for you; Java needs a hand-written Iterator.

using System.Runtime.CompilerServices;

namespace Shop.T19_Iterators;

/// <summary>Fake paged API ("GET /orders?page=N&amp;size=M") that counts how many pages were requested.</summary>
public sealed class OrderPages(IReadOnlyList<Order> source)
{
    public int PagesFetched { get; private set; }

    public IReadOnlyList<Order> Page(int index, int size)
    {
        PagesFetched++;
        return source.Skip(index * size).Take(size).ToList();
    }

    public async Task<IReadOnlyList<Order>> PageAsync(int index, int size, CancellationToken ct = default)
    {
        await Task.Delay(1, ct); // pretend network
        return Page(index, size);
    }
}

/// <summary>
/// yield return: the compiler writes the iterator state machine. Any control flow
/// (loops, ifs, try/finally) can produce a lazy sequence, and async streams
/// (IAsyncEnumerable + await foreach) do the same for asynchronous sources.
/// </summary>
public static class Iterators
{
    public static IEnumerable<DateOnly> DeliveryDays(DateOnly from)
    {
        for (var day = from.AddDays(1); ; day = day.AddDays(1))
        {
            if (day.DayOfWeek is not (DayOfWeek.Saturday or DayOfWeek.Sunday))
            {
                yield return day;
            }
        }
    }

    public static IEnumerable<Order> AllOrders(OrderPages api, int pageSize)
    {
        for (var page = 0; ; page++)
        {
            var items = api.Page(page, pageSize);
            if (items.Count == 0)
            {
                yield break;
            }
            foreach (var order in items)
            {
                yield return order;
            }
        }
    }

    public static async IAsyncEnumerable<Order> AllOrdersAsync(
        OrderPages api, int pageSize, [EnumeratorCancellation] CancellationToken ct = default)
    {
        for (var page = 0; ; page++)
        {
            var items = await api.PageAsync(page, pageSize, ct);
            if (items.Count == 0)
            {
                yield break;
            }
            foreach (var order in items)
            {
                yield return order;
            }
        }
    }
}
