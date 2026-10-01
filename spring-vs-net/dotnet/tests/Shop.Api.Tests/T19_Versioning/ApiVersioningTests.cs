// VERDICT | T19 API versioning | BETTER: SPRING
// WHY: Spring 7 routes by version (header/path/query/media type), rejects unsupported versions and sends Deprecation/Sunset headers; ASP.NET needs the third-party Asp.Versioning package or hand-written branching.

namespace Shop.Api.Tests.T19_Versioning;

public class ApiVersioningTests(ShopApiFactory factory) : IClassFixture<ShopApiFactory>
{
    private readonly HttpClient _client = factory.CreateClient();

    private Task<HttpResponseMessage> Get(string url, string? version = null)
    {
        var request = new HttpRequestMessage(HttpMethod.Get, url);
        if (version is not null)
        {
            request.Headers.Add("X-API-Version", version);
        }
        return _client.SendAsync(request);
    }

    [Fact]
    public async Task NoHeaderMeansVersionOneWithDeprecationHeaders()
    {
        var response = await Get("/api/catalog/E1");
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        Assert.Equal(89.99m, (await response.JsonAsync()).GetProperty("price").GetDecimal());
        Assert.True(response.Headers.Contains("Deprecation"));
        Assert.True(response.Headers.Contains("Sunset"));
    }

    [Fact]
    public async Task VersionTwoHasTheNewShape()
    {
        var response = await Get("/api/catalog/E1", "2");
        var body = await response.JsonAsync();
        Assert.Equal(89.99m, body.GetProperty("price").GetProperty("amount").GetDecimal());
        Assert.Equal("EUR", body.GetProperty("price").GetProperty("currency").GetString());
        Assert.True(body.GetProperty("inStock").GetBoolean());
        Assert.False(response.Headers.Contains("Deprecation"));
    }

    [Fact]
    public async Task UnsupportedVersionIsRejected()
    {
        Assert.Equal(HttpStatusCode.BadRequest, (await Get("/api/catalog/E1", "3")).StatusCode);
    }

    [Fact]
    public async Task UnversionedEndpointsAreUnaffected()
    {
        var response = await Get("/api/products/E1");
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        Assert.False(response.Headers.Contains("Deprecation"));
    }
}
