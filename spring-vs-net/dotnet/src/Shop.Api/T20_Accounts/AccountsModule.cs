// VERDICT | T20 User accounts | BETTER: ASP.NET
// WHY: ASP.NET Identity + MapIdentityApi give a user store, registration, login tokens, password rules, hashing and lockout; Spring Security authenticates, but accounts, registration and password rules are hand-written.

using Microsoft.AspNetCore.Identity;
using Shop.Api.T12_Security;

namespace Shop.Api.T20_Accounts;

/// <summary>
/// ASP.NET Identity: user store (EF Core tables), password hashing (PBKDF2), password policy,
/// lockout, e-mail confirmation, 2FA - and MapIdentityApi exposes /register, /login (bearer + refresh
/// tokens), /refresh, /manage/info, /forgotPassword, ... as ready-made endpoints.
/// </summary>
public static class AccountsModule
{
    private const string BasicOrBearer = "BasicOrBearer";

    public static IServiceCollection AddUserAccounts(this IServiceCollection services)
    {
        services.AddIdentityApiEndpoints<IdentityUser>(identity =>
            {
                identity.Lockout.MaxFailedAccessAttempts = 3;
                identity.Lockout.DefaultLockoutTimeSpan = TimeSpan.FromMinutes(5);
            })
            .AddEntityFrameworkStores<ShopDbContext>();

        // Two ways to log in live side by side: HTTP Basic (T12 demo users) and Identity bearer tokens.
        // A policy scheme picks one per request from the Authorization header.
        services.AddAuthentication(options => options.DefaultScheme = BasicOrBearer)
            .AddPolicyScheme(BasicOrBearer, "Basic (T12) or Identity bearer token (T20)", policy =>
                policy.ForwardDefaultSelector = http =>
                    http.Request.Headers.Authorization.ToString().StartsWith("Bearer ", StringComparison.OrdinalIgnoreCase)
                        ? IdentityConstants.BearerScheme
                        : BasicAuthenticationHandler.SchemeName);
        return services;
    }

    public static IEndpointRouteBuilder MapAccountEndpoints(this IEndpointRouteBuilder app)
    {
        app.MapGroup("/api/account").MapIdentityApi<IdentityUser>();
        return app;
    }
}
