// VERDICT | T16 OpenAPI | BETTER: ASP.NET
// WHY: AddOpenApi()/MapOpenApi() are first-party; Spring relies on the third-party springdoc project, which has to track every Boot release.

namespace Shop.Api.T16_OpenApi;

/// <summary>First-party OpenAPI document generation, served at /openapi/v1.json.</summary>
public static class OpenApiModule
{
    public static IServiceCollection AddShopOpenApi(this IServiceCollection services) =>
        services.AddOpenApi(options => options.AddDocumentTransformer((document, _, _) =>
        {
            document.Info.Title = "Shop API";
            return Task.CompletedTask;
        }));
}
