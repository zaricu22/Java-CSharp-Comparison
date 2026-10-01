// VERDICT | T14 Health checks | BETTER: SPRING
// WHY: Actuator returns JSON with details plus db/disk checks out of the box; ASP.NET health checks are simple but need a custom JSON response writer.

namespace Shop.Api.Tests.T14_Health;

public class HealthTests(ShopApiFactory factory) : IClassFixture<ShopApiFactory>
{
    [Fact]
    public async Task HealthIncludesTheCustomCheck()
    {
        var response = await factory.CreateClient().GetAsync("/health");
        var health = await response.JsonAsync();

        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        Assert.Equal("Healthy", health.GetProperty("status").GetString());
        var inventory = health.GetProperty("components").GetProperty("inventory");
        Assert.Equal("Healthy", inventory.GetProperty("status").GetString());
        Assert.Equal(5, inventory.GetProperty("details").GetProperty("products").GetInt32());
        Assert.Equal(1, inventory.GetProperty("details").GetProperty("outOfStock").GetInt32());
    }
}
