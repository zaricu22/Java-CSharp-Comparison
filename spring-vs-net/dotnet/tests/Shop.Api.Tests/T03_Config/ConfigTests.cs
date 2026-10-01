// VERDICT | T03 Configuration & profiles | BETTER: SPRING
// WHY: @Profile and @ConditionalOnProperty swap beans declaratively; ASP.NET binds options just as well, but conditional registration is hand-written if/else.

using Microsoft.AspNetCore.Hosting;
using Shop.Api.T03_Config;

namespace Shop.Api.Tests.T03_Config;

public class ConfigTests
{
    private static HttpClient Client(Action<IWebHostBuilder>? configure = null) =>
        new ShopApiFactory().WithWebHostBuilder(host => configure?.Invoke(host)).CreateClient();

    [Fact]
    public async Task DefaultEnvironmentUsesLogSenderAndDefaults()
    {
        var client = Client();
        var config = await client.GetFromJsonAsync<ConfigView>("/api/config", ShopApiFactory.Json);
        Assert.Equal(new ConfigView("EUR", 50m, "log", false), config);
        Assert.Equal(HttpStatusCode.NotFound, (await client.GetAsync("/api/recommendations/B1")).StatusCode);
    }

    [Fact]
    public async Task ProductionEnvironmentSwapsServiceAndOverridesValue()
    {
        var config = await Client(host => host.UseEnvironment("Production"))
            .GetFromJsonAsync<ConfigView>("/api/config", ShopApiFactory.Json);
        Assert.Equal("smtp", config!.NotificationChannel);
        Assert.Equal(100m, config.FreeShippingOver);
        Assert.Equal("EUR", config.Currency); // not overridden -> inherited from appsettings.json
    }

    [Fact]
    public async Task SettingTurnsTheConditionalServiceOn()
    {
        var client = Client(host => host.UseSetting("Shop:Features:Recommendations", "true"));
        Assert.True((await client.GetFromJsonAsync<ConfigView>("/api/config", ShopApiFactory.Json))!.Recommendations);
        var recommended = await client.GetFromJsonAsync<string[]>("/api/recommendations/B1");
        Assert.Equal(["Refactoring"], recommended!);
    }

    [Fact]
    public void InvalidSettingsStopTheApplication()
    {
        var error = Assert.ThrowsAny<Exception>(() => Client(host => host.UseSetting("Shop:Currency", "")));
        Assert.Contains("Currency", error.ToString());
    }
}
