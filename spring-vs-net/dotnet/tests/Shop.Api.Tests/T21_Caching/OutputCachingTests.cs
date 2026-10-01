// VERDICT | T21 Output caching | BETTER: ASP.NET
// WHY: OutputCache stores whole HTTP responses (vary by query, tag eviction) without touching service code; Spring has only method-level @Cacheable - the controller and JSON serialization still run.

using Shop.Api.T01_Endpoints;
using Shop.Api.T21_Caching;

namespace Shop.Api.Tests.T21_Caching;

public class OutputCachingTests
{
    private readonly ShopApiFactory _factory = new();
    private readonly HttpClient _client;
    private readonly CatalogQueryCounter _counter;

    public OutputCachingTests()
    {
        _client = _factory.CreateClient();
        _counter = _factory.Services.GetRequiredService<CatalogQueryCounter>();
    }

    private async Task<List<ProductDto>> Products(string category) =>
        (await _client.GetFromJsonAsync<List<ProductDto>>($"/api/cached/products?category={category}", ShopApiFactory.Json))!;

    [Fact]
    public async Task RepeatedRequestsAreServedFromTheCache()
    {
        await Products("Grocery");
        await Products("Grocery");
        Assert.Equal(1, _counter.Count); // the endpoint ran once; the second response came from the cache

        await Products("Electronics");
        Assert.Equal(2, _counter.Count); // varies by the query string
    }

    [Fact]
    public async Task UpdateEvictsByTag()
    {
        await Products("Books");
        var response = await _client.PutAsync("/api/cached/products/B1/price?price=30.00", null);
        Assert.Equal(HttpStatusCode.NoContent, response.StatusCode);

        var books = await Products("Books");
        Assert.Equal(30.00m, books.Single(p => p.Sku == "B1").Price);
        Assert.Equal(2, _counter.Count);
    }
}
