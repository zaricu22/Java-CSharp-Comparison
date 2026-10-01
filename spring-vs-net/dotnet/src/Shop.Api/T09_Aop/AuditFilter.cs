// VERDICT | T09 AOP / cross-cutting | BETTER: SPRING
// WHY: an @Aspect intercepts any bean method, including the service layer; ASP.NET endpoint filters and middleware only wrap HTTP requests.

using System.Collections.Concurrent;

namespace Shop.Api.T09_Aop;

public sealed class AuditTrail
{
    private readonly ConcurrentQueue<string> _entries = new();

    public void Record(string entry) => _entries.Enqueue(entry);

    public IReadOnlyList<string> Entries => _entries.ToList();
}

/// <summary>
/// The closest built-in thing to an aspect: an endpoint filter. It wraps HTTP endpoints that
/// opt in with .AddEndpointFilter&lt;AuditFilter&gt;() - calls to OrderService from a job,
/// another service or a test bypass it. Service-level interception needs hand-written
/// decorators (or a library such as Scrutor / Castle DynamicProxy).
/// </summary>
public sealed class AuditFilter(AuditTrail trail) : IEndpointFilter
{
    public async ValueTask<object?> InvokeAsync(EndpointFilterInvocationContext context, EndpointFilterDelegate next)
    {
        var name = context.HttpContext.GetEndpoint()?.Metadata.GetMetadata<IEndpointNameMetadata>()?.EndpointName ?? "endpoint";
        try
        {
            var result = await next(context);
            var inner = result is INestedHttpResult nested ? nested.Result : result;
            trail.Record(inner is IStatusCodeHttpResult { StatusCode: >= 400 } failed
                ? $"{name} failed: {failed.StatusCode}"
                : $"{name} ok");
            return result;
        }
        catch (Exception e)
        {
            trail.Record($"{name} failed: {e.GetType().Name}");
            throw;
        }
    }
}

public static class AuditModule
{
    public static IServiceCollection AddAuditing(this IServiceCollection services) => services.AddSingleton<AuditTrail>();
}
