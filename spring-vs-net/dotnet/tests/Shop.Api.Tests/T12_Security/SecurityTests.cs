// VERDICT | T12 Security | BETTER: SPRING
// WHY: Spring Security ships HTTP Basic, in-memory users and @PreAuthorize expressions over method arguments; ASP.NET needs a custom Basic handler and an authorization handler for the same rules.

using System.Net.Http.Headers;
using System.Text;

namespace Shop.Api.Tests.T12_Security;

public class SecurityTests(ShopApiFactory factory) : IClassFixture<ShopApiFactory>
{
    private const string Restock = "/api/admin/products/E1/restock";

    private readonly HttpClient _client = factory.CreateClient();

    private Task<HttpResponseMessage> PostAs(string? user, string? password, int quantity)
    {
        var request = new HttpRequestMessage(HttpMethod.Post, $"{Restock}?quantity={quantity}");
        if (user is not null)
        {
            request.Headers.Authorization = new AuthenticationHeaderValue("Basic",
                Convert.ToBase64String(Encoding.UTF8.GetBytes($"{user}:{password}")));
        }
        return _client.SendAsync(request);
    }

    [Fact]
    public async Task AnonymousIsUnauthorized()
    {
        var anonymous = await PostAs(null, null, 10);
        Assert.Equal(HttpStatusCode.Unauthorized, anonymous.StatusCode);
        Assert.Equal("Basic", anonymous.Headers.WwwAuthenticate.First().Scheme);
        Assert.Equal(HttpStatusCode.Unauthorized, (await PostAs("admin", "wrong", 10)).StatusCode);
    }

    [Fact]
    public async Task ClerkIsForbiddenByTheServiceCheck()
    {
        Assert.Equal(HttpStatusCode.Forbidden, (await PostAs("clerk", "clerk-pass", 10)).StatusCode);
    }

    [Fact]
    public async Task AdminCanRestockWithinTheLimit()
    {
        var response = await PostAs("admin", "admin-pass", 10);
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        Assert.Equal(13, (await response.JsonAsync()).GetProperty("stock").GetInt32());
    }

    [Fact]
    public async Task RuleAlsoChecksTheArgument()
    {
        Assert.Equal(HttpStatusCode.Forbidden, (await PostAs("admin", "admin-pass", 5000)).StatusCode);
    }
}
