// VERDICT | T14 Health checks | BETTER: SPRING
// WHY: Actuator returns JSON with details plus db/disk checks out of the box; ASP.NET health checks are simple but need a custom JSON response writer.

using Microsoft.AspNetCore.Diagnostics.HealthChecks;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Diagnostics.HealthChecks;

namespace Shop.Api.T14_Health;

public sealed class InventoryHealthCheck(ShopDbContext db) : IHealthCheck
{
    public async Task<HealthCheckResult> CheckHealthAsync(HealthCheckContext context, CancellationToken ct = default)
    {
        var total = await db.Products.CountAsync(ct);
        if (total == 0)
        {
            return HealthCheckResult.Unhealthy("catalog is empty");
        }
        var data = new Dictionary<string, object> { ["products"] = total, ["outOfStock"] = await db.Products.CountAsync(p => p.Stock < 1, ct) };
        return HealthCheckResult.Healthy(data: data);
    }
}

public static class HealthModule
{
    public static IServiceCollection AddShopHealthChecks(this IServiceCollection services)
    {
        services.AddHealthChecks().AddCheck<InventoryHealthCheck>("inventory");
        // A database check would need an extra package (Microsoft.Extensions.Diagnostics.HealthChecks.EntityFrameworkCore)
        return services;
    }

    /// <summary>The default response is the plain text "Healthy" - JSON with details needs a writer.</summary>
    public static IEndpointRouteBuilder MapShopHealthChecks(this IEndpointRouteBuilder app)
    {
        app.MapHealthChecks("/health", new HealthCheckOptions
        {
            ResponseWriter = (http, report) => http.Response.WriteAsJsonAsync(new
            {
                status = report.Status.ToString(),
                components = report.Entries.ToDictionary(
                    entry => entry.Key,
                    entry => new { status = entry.Value.Status.ToString(), details = entry.Value.Data }),
            }),
        });
        return app;
    }
}
