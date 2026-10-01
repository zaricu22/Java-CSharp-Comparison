// VERDICT | T11 Resilience (retry) | BETTER: TIE
// WHY: Spring Framework 7 has @Retryable built in; .NET has Microsoft.Extensions.Resilience (Polly v8) pipelines - both are first-party today.

using Shop.Api.T11_Resilience;

namespace Shop.Api.Tests.T11_Resilience;

public class ResilienceTests(ShopApiFactory factory) : IClassFixture<ShopApiFactory>
{
    private readonly HttpClient _client = factory.CreateClient();
    private readonly FlakyRatesApi _api = factory.Services.GetRequiredService<FlakyRatesApi>();

    [Fact]
    public async Task TransientFailuresAreRetried()
    {
        _api.FailNext(2);
        var response = await _client.GetAsync("/api/rates/USD");

        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        Assert.Equal(1.08m, (await response.JsonAsync()).GetProperty("rate").GetDecimal());
        Assert.Equal(3, _api.Calls); // 1 call + 2 retries
    }

    [Fact]
    public async Task GivesUpAfterMaxRetries()
    {
        _api.FailNext(10);
        var response = await _client.GetAsync("/api/rates/USD");

        Assert.Equal(HttpStatusCode.ServiceUnavailable, response.StatusCode);
        Assert.Equal(4, _api.Calls); // 1 call + 3 retries
    }
}
