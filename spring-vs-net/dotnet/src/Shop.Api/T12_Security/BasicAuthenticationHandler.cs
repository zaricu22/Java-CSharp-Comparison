// VERDICT | T12 Security | BETTER: SPRING
// WHY: Spring Security ships HTTP Basic, in-memory users and @PreAuthorize expressions over method arguments; ASP.NET needs a custom Basic handler and an authorization handler for the same rules.

using System.Net.Http.Headers;
using System.Security.Claims;
using System.Text;
using System.Text.Encodings.Web;
using Microsoft.AspNetCore.Authentication;
using Microsoft.Extensions.Options;

namespace Shop.Api.T12_Security;

/// <summary>Demo users (plain-text passwords, demos and tests only). ASP.NET has no in-memory user store.</summary>
public sealed class DemoUsers
{
    private readonly Dictionary<string, (string Password, string[] Roles)> _users = new()
    {
        ["admin"] = ("admin-pass", ["Admin"]),
        ["clerk"] = ("clerk-pass", ["Clerk"]),
    };

    public string[]? Validate(string user, string password) =>
        _users.TryGetValue(user, out var entry) && entry.Password == password ? entry.Roles : null;
}

/// <summary>ASP.NET Core has no HTTP Basic handler built in (Spring: http.httpBasic()), so here is one.</summary>
public sealed class BasicAuthenticationHandler(
    IOptionsMonitor<AuthenticationSchemeOptions> options, ILoggerFactory logger, UrlEncoder encoder, DemoUsers users)
    : AuthenticationHandler<AuthenticationSchemeOptions>(options, logger, encoder)
{
    public const string SchemeName = "Basic";

    protected override Task<AuthenticateResult> HandleAuthenticateAsync()
    {
        if (!AuthenticationHeaderValue.TryParse(Request.Headers.Authorization, out var header) || header.Scheme != SchemeName)
        {
            return Task.FromResult(AuthenticateResult.NoResult());
        }

        string[] credentials;
        try
        {
            credentials = Encoding.UTF8.GetString(Convert.FromBase64String(header.Parameter ?? "")).Split(':', 2);
        }
        catch (FormatException)
        {
            return Task.FromResult(AuthenticateResult.Fail("Malformed credentials"));
        }

        if (credentials.Length != 2 || users.Validate(credentials[0], credentials[1]) is not { } roles)
        {
            return Task.FromResult(AuthenticateResult.Fail("Invalid user name or password"));
        }

        var claims = new List<Claim> { new(ClaimTypes.Name, credentials[0]) };
        claims.AddRange(roles.Select(role => new Claim(ClaimTypes.Role, role)));
        var principal = new ClaimsPrincipal(new ClaimsIdentity(claims, SchemeName));
        return Task.FromResult(AuthenticateResult.Success(new AuthenticationTicket(principal, SchemeName)));
    }

    protected override Task HandleChallengeAsync(AuthenticationProperties properties)
    {
        Response.Headers.WWWAuthenticate = "Basic realm=\"shop\"";
        return base.HandleChallengeAsync(properties);
    }
}
