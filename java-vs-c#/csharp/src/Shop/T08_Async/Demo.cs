// VERDICT | T08 Async | BETTER: TIE
// WHY: C# async/await beats CompletableFuture; Java 21+ virtual threads allow plain blocking code with no async 'coloring'.

using System.Diagnostics;

namespace Shop.T08_Async;

public static class Demo
{
    public static async Task RunAsync()
    {
        var watch = Stopwatch.StartNew();
        var views = await CatalogService.ViewsAsync(["B1", "E1", "G1"]);
        Console.WriteLine($"async/await: [{string.Join(", ", views.AsEnumerable())}] in {watch.ElapsedMilliseconds} ms");

        Console.WriteLine($"Unknown sku price: {await CatalogService.PriceOrDefaultAsync("X9", 0m)}");
    }
}
