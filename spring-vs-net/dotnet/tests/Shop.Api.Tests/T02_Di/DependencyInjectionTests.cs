// VERDICT | T02 Dependency injection | BETTER: SPRING
// WHY: component scanning, Map<name, bean> injection and scoped proxies (request bean inside a singleton); ASP.NET registers everything by hand (easier to trace).

using Shop.Api.T02_Di;

namespace Shop.Api.Tests.T02_Di;

public class DependencyInjectionTests(ShopApiFactory factory) : IClassFixture<ShopApiFactory>
{
    private readonly HttpClient _client = factory.CreateClient();

    [Fact]
    public async Task KeyedImplementationsAreRegisteredByHand()
    {
        var methods = await _client.GetFromJsonAsync<string[]>("/api/payments");
        Assert.Equal(["bank", "card", "paypal"], methods!);
        Assert.NotNull(factory.Services.GetKeyedService<IPaymentProvider>("card"));
    }

    [Fact]
    public async Task FeeComesFromTheKeyedImplementation()
    {
        Assert.Equal(1.75m, (await _client.GetJsonAsync("/api/payments/card/fee?amount=100")).GetProperty("fee").GetDecimal());
        Assert.Equal(3.25m, (await _client.GetJsonAsync("/api/payments/paypal/fee?amount=100")).GetProperty("fee").GetDecimal());
        Assert.Equal(HttpStatusCode.NotFound, (await _client.GetAsync("/api/payments/crypto/fee?amount=100")).StatusCode);
    }

    [Fact]
    public async Task ScopedServicesAreSharedWithinOneRequest()
    {
        var first = await _client.GetJsonAsync("/api/di/request-ids");
        var second = await _client.GetJsonAsync("/api/di/request-ids");
        Assert.Equal(first.GetProperty("controller").GetString(), first.GetProperty("scopedLogger").GetString());
        Assert.NotEqual(first.GetProperty("controller").GetString(), second.GetProperty("controller").GetString());
    }

    [Fact]
    public void SingletonCannotDependOnScoped()
    {
        // Spring injects a scoped proxy here; .NET rejects the "captive dependency" when validation is on
        // (it is on by default in the Development environment).
        var services = new ServiceCollection().AddScoped<RequestInfo>().AddSingleton<RequestLogger>();
        var error = Assert.Throws<AggregateException>(() =>
            services.BuildServiceProvider(new ServiceProviderOptions { ValidateScopes = true, ValidateOnBuild = true }));
        Assert.Contains("Cannot consume scoped service", error.InnerExceptions[0].Message);
    }
}
