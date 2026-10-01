// VERDICT | T01 Endpoints & hosting | BETTER: ASP.NET
// WHY: minimal APIs map a route to a lambda with typed results in one line; Spring needs a @RestController class (Java 21 sealed switch maps results as cleanly as C#).

using System.Diagnostics;
using Microsoft.AspNetCore.Http.HttpResults;
using Shop.Api.T04_Validation;
using Shop.Api.T09_Aop;

namespace Shop.Api.T01_Endpoints;

/// <summary>
/// Minimal APIs: a route is a lambda; parameters bind from route/query/body/DI by type.
/// Results&lt;...&gt; declares every possible response, which also feeds OpenAPI (T16).
/// Handlers are async (Task) end to end - the Spring side uses virtual threads instead.
/// </summary>
public static class ProductEndpoints
{
    public static IServiceCollection AddCatalog(this IServiceCollection services) =>
        services.AddScoped<ICatalogService, CatalogService>();

    public static IEndpointRouteBuilder MapProductEndpoints(this IEndpointRouteBuilder app)
    {
        var products = app.MapGroup("/api/products");

        products.MapGet("/", (Category? category, ICatalogService catalog) => catalog.ListAsync(category));

        products.MapGet("/{sku}", async Task<Results<Ok<ProductDto>, ProblemHttpResult>> (string sku, ICatalogService catalog) =>
            await catalog.FindAsync(sku) is { } product
                ? TypedResults.Ok(product)
                : TypedResults.Problem(statusCode: StatusCodes.Status404NotFound, detail: $"Product {sku} not found"));

        products.MapPost("/", async Task<Results<Created<ProductDto>, ProblemHttpResult>> (NewProduct body, ICatalogService catalog) =>
                await catalog.RegisterAsync(body) switch
                {
                    RegisterResult.Created(var product) => TypedResults.Created($"/api/products/{product.Sku}", product),
                    RegisterResult.DuplicateSku(var sku) =>
                        TypedResults.Problem(statusCode: StatusCodes.Status409Conflict, detail: $"SKU {sku} already exists"),
                    _ => throw new UnreachableException(), // C# cannot prove the record hierarchy is closed
                })
            .WithName("RegisterProduct")
            .AddEndpointFilter<AuditFilter>();

        return app;
    }
}
