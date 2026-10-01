// VERDICT | T07 Transactions | BETTER: SPRING
// WHY: @Transactional makes a whole service method atomic across repositories and nested calls; EF's SaveChanges is one unit of work, several saves need BeginTransaction/Commit.

using Shop.Api.T01_Endpoints;
using Shop.Api.T07_Transactions;

namespace Shop.Api.Tests.T07_Transactions;

public class TransactionsTests
{
    private static OrderRequest Order(params (string Sku, int Quantity)[] lines) =>
        new("Eva", lines.Select(l => new OrderLineRequest(l.Sku, l.Quantity)).ToList());

    private static async Task<int> StockOf(HttpClient client, string sku) =>
        (await client.GetFromJsonAsync<ProductDto>($"/api/products/{sku}", ShopApiFactory.Json))!.Stock;

    [Fact]
    public async Task SuccessfulOrderReducesStock()
    {
        var client = new ShopApiFactory().CreateClient();
        var response = await client.PostAsJsonAsync("/api/orders", Order(("B1", 2)));

        Assert.Equal(HttpStatusCode.Created, response.StatusCode);
        Assert.Equal(71.00m, (await response.Content.ReadFromJsonAsync<OrderConfirmation>())!.Total);
        Assert.Equal(10, await StockOf(client, "B1"));
    }

    [Fact]
    public async Task FailureOnSecondLineRollsBackTheFirst()
    {
        // B2 has 7 in stock, E2 has 0: the B2 change is made first, then E2 fails
        var client = new ShopApiFactory().CreateClient();
        var response = await client.PostAsJsonAsync("/api/orders", Order(("B2", 1), ("E2", 1)));

        Assert.Equal(HttpStatusCode.Conflict, response.StatusCode);
        Assert.Equal("Only 0 of E2 in stock, 1 requested", (await response.JsonAsync()).GetProperty("detail").GetString());
        Assert.Equal(7, await StockOf(client, "B2"));
    }

    [Fact]
    public async Task UnknownProductIs404()
    {
        var client = new ShopApiFactory().CreateClient();
        var response = await client.PostAsJsonAsync("/api/orders", Order(("X9", 1)));
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }
}
