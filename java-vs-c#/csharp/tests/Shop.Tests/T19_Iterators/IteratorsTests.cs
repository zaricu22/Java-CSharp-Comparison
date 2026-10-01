// VERDICT | T19 Iterators & generators | BETTER: C#
// WHY: yield return and async streams (await foreach) write the state machine for you; Java needs a hand-written Iterator.

namespace Shop.T19_Iterators;

public class IteratorsTests
{
    [Fact]
    public void InfiniteSequenceSkipsWeekends()
    {
        Assert.Equal([new DateOnly(2026, 1, 12), new DateOnly(2026, 1, 13), new DateOnly(2026, 1, 14)],
            Iterators.DeliveryDays(new DateOnly(2026, 1, 9)).Take(3));
    }

    [Fact]
    public void PagedIteratorReadsUntilEmptyPage()
    {
        var api = new OrderPages(SampleData.Orders);
        Assert.Equal([1, 2, 3, 4], Iterators.AllOrders(api, 3).Select(o => o.Id));
        Assert.Equal(3, api.PagesFetched); // [1,2,3], [4], []
    }

    [Fact]
    public void PagedIteratorIsLazy()
    {
        var api = new OrderPages(SampleData.Orders);
        Assert.Equal([1, 2], Iterators.AllOrders(api, 3).Take(2).Select(o => o.Id));
        Assert.Equal(1, api.PagesFetched);
    }

    [Fact]
    public async Task AsyncStreamWithAwaitForeach()
    {
        var api = new OrderPages(SampleData.Orders);
        var ids = new List<int>();
        await foreach (var order in Iterators.AllOrdersAsync(api, 2))
        {
            ids.Add(order.Id);
        }
        Assert.Equal([1, 2, 3, 4], ids);
        Assert.Equal(3, api.PagesFetched);
    }
}
