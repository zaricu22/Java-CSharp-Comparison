// VERDICT | T13 Rate limiting | BETTER: ASP.NET
// WHY: AddRateLimiter + RequireRateLimiting are built in and partitioned per client; Spring has no rate limiter, so it is a hand-written filter (or Bucket4j).

using System.Threading.RateLimiting;
using Microsoft.Extensions.Options;
using Shop.Api.T01_Endpoints;
using Shop.Api.T03_Config;

namespace Shop.Api.T13_RateLimiting;

/// <summary>Built-in middleware: fixed window, sliding window, token bucket and concurrency limiters.</summary>
public static class RateLimitingModule
{
    public const string SearchPolicy = "search";

    public static IServiceCollection AddSearchRateLimiting(this IServiceCollection services) =>
        services.AddRateLimiter(limiter =>
        {
            limiter.RejectionStatusCode = StatusCodes.Status429TooManyRequests;
            limiter.AddPolicy(SearchPolicy, http =>
            {
                var settings = http.RequestServices.GetRequiredService<IOptions<ShopOptions>>().Value.RateLimit;
                return RateLimitPartition.GetFixedWindowLimiter(
                    partitionKey: http.Request.Headers["X-Client-Id"].ToString(),
                    _ => new FixedWindowRateLimiterOptions { PermitLimit = settings.Permits, Window = settings.Window, QueueLimit = 0 });
            });
        });

    public static IEndpointRouteBuilder MapSearchEndpoints(this IEndpointRouteBuilder app)
    {
        app.MapGet("/api/search", (string q, ICatalogService catalog) => catalog.SearchAsync(q))
            .RequireRateLimiting(SearchPolicy);
        return app;
    }
}
