// VERDICT | T08 Async | BETTER: TIE
// WHY: C# async/await beats CompletableFuture; Java 21+ virtual threads allow plain blocking code with no async 'coloring'.

using System.Diagnostics;

namespace Shop.T08_Async;

public class AsyncTests
{
    private static readonly string[] Skus = ["B1", "E1", "G1"];
    private static readonly ProductView Keyboard = new("E1", 89.99m, 3);

    /// <summary>3 products x 2 calls x 100 ms = 600 ms sequentially; concurrently it is ~100 ms.</summary>
    private const long ConcurrentLimitMs = 400;

    [Fact]
    public async Task AwaitCombinesPriceAndStock()
    {
        Assert.Equal(Keyboard, await CatalogService.ViewAsync("E1"));
    }

    [Fact]
    public async Task TasksRunConcurrently()
    {
        var watch = Stopwatch.StartNew();
        var views = await CatalogService.ViewsAsync(Skus);

        Assert.Equal(3, views.Length);
        Assert.Equal(Keyboard, views[1]);
        Assert.True(watch.ElapsedMilliseconds < ConcurrentLimitMs, $"took {watch.ElapsedMilliseconds} ms");
    }

    [Fact]
    public async Task ErrorsArriveUnwrapped()
    {
        await Assert.ThrowsAsync<KeyNotFoundException>(() => RemoteApis.PriceAsync("X9"));
        Assert.Equal(0m, await CatalogService.PriceOrDefaultAsync("X9", 0m));
        Assert.Equal(35.50m, await CatalogService.PriceOrDefaultAsync("B1", 0m));
    }

    [Fact]
    public async Task CancellationIsBuiltIn()
    {
        using var cts = new CancellationTokenSource();
        await cts.CancelAsync();
        await Assert.ThrowsAnyAsync<OperationCanceledException>(() => CatalogService.ViewsAsync(Skus, cts.Token));
    }
}
