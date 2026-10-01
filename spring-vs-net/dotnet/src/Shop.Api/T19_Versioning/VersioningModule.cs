// VERDICT | T19 API versioning | BETTER: SPRING
// WHY: Spring 7 routes by version (header/path/query/media type), rejects unsupported versions and sends Deprecation/Sunset headers; ASP.NET needs the third-party Asp.Versioning package or hand-written branching.

using Microsoft.Extensions.Options;
using Shop.Api.T01_Endpoints;
using Shop.Api.T03_Config;

namespace Shop.Api.T19_Versioning;

public record ProductV1(string Sku, string Name, decimal Price);

public record Money(decimal Amount, string Currency);

public record ProductV2(string Sku, string Name, Money Price, bool InStock);

/// <summary>
/// ASP.NET Core has no API versioning of its own (the Asp.Versioning package is a separate,
/// community-maintained project). Without it: read the header, validate it, branch, and add the
/// RFC 9745 Deprecation / RFC 8594 Sunset headers by hand.
/// </summary>
public static class VersioningModule
{
    private const string VersionHeader = "X-API-Version";
    private static readonly DateTimeOffset V1Deprecated = new(2026, 1, 1, 0, 0, 0, TimeSpan.Zero);
    private static readonly DateTimeOffset V1Sunset = new(2026, 12, 31, 0, 0, 0, TimeSpan.Zero);

    public static IEndpointRouteBuilder MapVersionedCatalog(this IEndpointRouteBuilder app)
    {
        app.MapGet("/api/catalog/{sku}", async (string sku, HttpContext http, ICatalogService catalog, IOptions<ShopOptions> options) =>
        {
            var version = http.Request.Headers[VersionHeader].FirstOrDefault() ?? "1";
            if (version is not ("1" or "1.0" or "2" or "2.0"))
            {
                return Results.Problem(statusCode: StatusCodes.Status400BadRequest, detail: $"Unsupported API version {version}");
            }
            if (await catalog.FindAsync(sku) is not { } product)
            {
                return Results.Problem(statusCode: StatusCodes.Status404NotFound, detail: $"Product {sku} not found");
            }
            if (version.StartsWith('1'))
            {
                http.Response.Headers["Deprecation"] = $"@{V1Deprecated.ToUnixTimeSeconds()}";
                http.Response.Headers["Sunset"] = V1Sunset.ToString("R");
                return Results.Ok(new ProductV1(product.Sku, product.Name, product.Price));
            }
            return Results.Ok(new ProductV2(product.Sku, product.Name,
                new Money(product.Price, options.Value.Currency), product.Stock > 0));
        });
        return app;
    }
}
