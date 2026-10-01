// VERDICT | T13 Rate limiting | BETTER: ASP.NET
// WHY: AddRateLimiter + RequireRateLimiting are built in and partitioned per client; Spring has no rate limiter, so it is a hand-written filter (or Bucket4j).

using Shop.Api.T01_Endpoints;

namespace Shop.Api.Tests.T13_RateLimiting;

public class RateLimitTests(ShopApiFactory factory) : IClassFixture<ShopApiFactory>
{
    private readonly HttpClient _client = factory.CreateClient();

    private Task<HttpResponseMessage> Search(string q, string clientId)
    {
        var request = new HttpRequestMessage(HttpMethod.Get, $"/api/search?q={q}");
        request.Headers.Add("X-Client-Id", clientId);
        return _client.SendAsync(request);
    }

    [Fact]
    public async Task SearchFindsByName()
    {
        var products = await (await Search("co", "t13-search")).Content.ReadFromJsonAsync<List<ProductDto>>(ShopApiFactory.Json);
        Assert.Equal(["B1", "G1"], products!.Select(p => p.Sku)); // Clean Code, Coffee
    }

    [Fact]
    public async Task FourthRequestInTheWindowIsRejected()
    {
        for (var i = 0; i < 3; i++)
        {
            Assert.Equal(HttpStatusCode.OK, (await Search("a", "t13-a")).StatusCode);
        }
        Assert.Equal(HttpStatusCode.TooManyRequests, (await Search("a", "t13-a")).StatusCode);
        Assert.Equal(HttpStatusCode.OK, (await Search("a", "t13-b")).StatusCode); // other clients have their own window
    }
}
