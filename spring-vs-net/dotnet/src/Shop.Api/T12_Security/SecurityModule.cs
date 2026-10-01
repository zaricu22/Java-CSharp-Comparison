// VERDICT | T12 Security | BETTER: SPRING
// WHY: Spring Security ships HTTP Basic, in-memory users and @PreAuthorize expressions over method arguments; ASP.NET needs a custom Basic handler and an authorization handler for the same rules.

using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;

namespace Shop.Api.T12_Security;

public static class SecurityModule
{
    public const string RestockPolicy = "Restock";

    public static IServiceCollection AddShopSecurity(this IServiceCollection services)
    {
        services.AddSingleton<DemoUsers>();
        services.AddAuthentication(BasicAuthenticationHandler.SchemeName)
            .AddScheme<AuthenticationSchemeOptions, BasicAuthenticationHandler>(BasicAuthenticationHandler.SchemeName, null);
        services.AddAuthorizationBuilder()
            .AddPolicy(RestockPolicy, policy => policy.RequireAuthenticatedUser().AddRequirements(new RestockRequirement(1000)));
        services.AddSingleton<IAuthorizationHandler, RestockHandler>();
        services.AddScoped<InventoryService>();
        return services;
    }

    public static IEndpointRouteBuilder MapAdminEndpoints(this IEndpointRouteBuilder app)
    {
        var admin = app.MapGroup("/api/admin").RequireAuthorization(); // who may call is decided per method

        admin.MapPost("/products/{sku}/restock", async (string sku, int quantity, ClaimsPrincipal user, InventoryService inventory) =>
        {
            try
            {
                return Results.Ok(await inventory.RestockAsync(user, sku, quantity));
            }
            catch (ForbiddenException)
            {
                return Results.Forbid();
            }
        });

        return app;
    }
}
