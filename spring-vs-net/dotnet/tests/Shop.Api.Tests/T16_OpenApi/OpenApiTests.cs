// VERDICT | T16 OpenAPI | BETTER: ASP.NET
// WHY: AddOpenApi()/MapOpenApi() are first-party; Spring relies on the third-party springdoc project, which has to track every Boot release.

namespace Shop.Api.Tests.T16_OpenApi;

public class OpenApiTests(ShopApiFactory factory) : IClassFixture<ShopApiFactory>
{
    [Fact]
    public async Task DocumentDescribesTheEndpoints()
    {
        var response = await factory.CreateClient().GetAsync("/openapi/v1.json");
        var document = await response.JsonAsync();

        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        Assert.Equal("Shop API", document.GetProperty("info").GetProperty("title").GetString());
        Assert.True(document.GetProperty("paths").TryGetProperty("/api/products", out _));
        Assert.True(document.GetProperty("paths").TryGetProperty("/api/orders", out _));
    }
}
