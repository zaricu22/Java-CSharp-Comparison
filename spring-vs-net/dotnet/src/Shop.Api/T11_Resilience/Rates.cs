// VERDICT | T11 Resilience (retry) | BETTER: TIE
// WHY: Spring Framework 7 has @Retryable built in; .NET has Microsoft.Extensions.Resilience (Polly v8) pipelines - both are first-party today.

using Polly;
using Polly.Registry;
using Polly.Retry;

namespace Shop.Api.T11_Resilience;

public class TransientRateException() : Exception("rates API temporarily unavailable");

/// <summary>Stand-in for a remote exchange-rate API that can be told to fail the next N calls.</summary>
public sealed class FlakyRatesApi
{
    private static readonly Dictionary<string, decimal> RatesByCurrency = new() { ["USD"] = 1.08m, ["RSD"] = 117.20m };

    private int _failuresLeft;
    private int _calls;

    public int Calls => _calls;

    public void FailNext(int times)
    {
        _failuresLeft = times;
        _calls = 0;
    }

    public decimal Rate(string currency)
    {
        Interlocked.Increment(ref _calls);
        if (Interlocked.Decrement(ref _failuresLeft) >= 0)
        {
            throw new TransientRateException();
        }
        return RatesByCurrency.TryGetValue(currency, out var rate) ? rate : throw new ArgumentException($"Unknown currency {currency}");
    }
}

/// <summary>The retry policy is a named pipeline configured at startup and applied explicitly around the call.</summary>
public sealed class ExchangeRateClient(FlakyRatesApi api, ResiliencePipelineProvider<string> pipelines)
{
    private readonly ResiliencePipeline _pipeline = pipelines.GetPipeline(RatesModule.Pipeline);

    public ValueTask<decimal> RateAsync(string currency, CancellationToken ct = default) =>
        _pipeline.ExecuteAsync(_ => ValueTask.FromResult(api.Rate(currency)), ct);
}

public static class RatesModule
{
    public const string Pipeline = "rates";

    public static IServiceCollection AddRates(this IServiceCollection services)
    {
        services.AddSingleton<FlakyRatesApi>();
        services.AddSingleton<ExchangeRateClient>();
        services.AddResiliencePipeline(Pipeline, pipeline => pipeline.AddRetry(new RetryStrategyOptions
        {
            ShouldHandle = new PredicateBuilder().Handle<TransientRateException>(),
            MaxRetryAttempts = 3,
            Delay = TimeSpan.FromMilliseconds(10),
            BackoffType = DelayBackoffType.Constant,
        }));
        return services;
    }

    public static IEndpointRouteBuilder MapRateEndpoints(this IEndpointRouteBuilder app)
    {
        app.MapGet("/api/rates/{currency}", async (string currency, ExchangeRateClient rates) =>
        {
            try
            {
                return Results.Ok(new { currency, rate = await rates.RateAsync(currency) });
            }
            catch (TransientRateException e)
            {
                return Results.Problem(statusCode: StatusCodes.Status503ServiceUnavailable, detail: e.Message);
            }
        });
        return app;
    }
}
