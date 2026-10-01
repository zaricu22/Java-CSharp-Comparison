// VERDICT | T01 Endpoints & hosting | BETTER: ASP.NET
// WHY: minimal APIs map a route to a lambda with typed results in one line; Spring needs a @RestController class (Java 21 sealed switch maps results as cleanly as C#).

using Shop.Api.T01_Endpoints;

namespace Shop.Api.Tests.T01_Endpoints;

public class ProductEndpointsTests(ShopApiFactory factory) : IClassFixture<ShopApiFactory>
{
    private readonly HttpClient _client = factory.CreateClient();

    [Fact]
    public async Task ListsAllProducts()
    {
        var products = await _client.GetFromJsonAsync<List<ProductDto>>("/api/products", ShopApiFactory.Json);
        Assert.Equal(5, products!.Count);
        Assert.Equal("B1", products[0].Sku);
        Assert.Equal(35.5m, products[0].Price);
    }

    [Fact]
    public async Task FiltersByCategory()
    {
        var books = await _client.GetFromJsonAsync<List<ProductDto>>("/api/products?category=Books", ShopApiFactory.Json);
        Assert.Equal(["B1", "B2"], books!.Select(p => p.Sku));
    }

    [Fact]
    public async Task GetsOneProduct()
    {
        var keyboard = await _client.GetFromJsonAsync<ProductDto>("/api/products/E1", ShopApiFactory.Json);
        Assert.Equal("Keyboard", keyboard!.Name);
        Assert.Equal(3, keyboard.Stock);
    }

    [Fact]
    public async Task UnknownProductIsProblemDetails404()
    {
        var response = await _client.GetAsync("/api/products/X9");
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
        Assert.Equal("application/problem+json", response.Content.Headers.ContentType?.MediaType);
        Assert.Equal("Product X9 not found", (await response.JsonAsync()).GetProperty("detail").GetString());
    }
}
