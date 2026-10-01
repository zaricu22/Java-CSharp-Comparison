// VERDICT | T20 User accounts | BETTER: ASP.NET
// WHY: ASP.NET Identity + MapIdentityApi give a user store, registration, login tokens, password rules, hashing and lockout; Spring Security authenticates, but accounts, registration and password rules are hand-written.

using System.Net.Http.Headers;
using Microsoft.EntityFrameworkCore;

namespace Shop.Api.Tests.T20_Accounts;

public class AccountsTests
{
    private readonly ShopApiFactory _factory = new();
    private readonly HttpClient _client;

    public AccountsTests() => _client = _factory.CreateClient();

    private Task<HttpResponseMessage> Register(string email, string password) =>
        _client.PostAsJsonAsync("/api/account/register", new { email, password });

    private Task<HttpResponseMessage> Login(string email, string password) =>
        _client.PostAsJsonAsync("/api/account/login", new { email, password });

    private static async Task<string[]> ErrorCodes(HttpResponseMessage response) =>
        (await response.JsonAsync()).GetProperty("errors").EnumerateObject().Select(e => e.Name).ToArray();

    [Fact]
    public async Task RegisterLoginAndCallAProtectedEndpoint()
    {
        Assert.Equal(HttpStatusCode.OK, (await Register("eva@shop.rs", "Secret1!")).StatusCode);

        var login = await Login("eva@shop.rs", "Secret1!");
        var token = (await login.JsonAsync()).GetProperty("accessToken").GetString();

        var request = new HttpRequestMessage(HttpMethod.Get, "/api/account/manage/info");
        request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", token);
        var info = await _client.SendAsync(request);
        Assert.Equal("eva@shop.rs", (await info.JsonAsync()).GetProperty("email").GetString());
    }

    [Fact]
    public async Task WeakPasswordListsEveryBrokenRule()
    {
        var response = await Register("weak@shop.rs", "abc");
        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
        Assert.Equal(
            ["PasswordRequiresDigit", "PasswordRequiresNonAlphanumeric", "PasswordRequiresUpper", "PasswordTooShort"],
            (await ErrorCodes(response)).Order());
    }

    [Fact]
    public async Task DuplicateEmailIsRejected()
    {
        await Register("dup@shop.rs", "Secret1!");
        var response = await Register("dup@shop.rs", "Secret1!");
        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
        Assert.Contains("DuplicateUserName", await ErrorCodes(response));
    }

    [Fact]
    public async Task PasswordIsStoredHashed()
    {
        await Register("hash@shop.rs", "Secret1!");
        using var scope = _factory.Services.CreateScope();
        var user = await scope.ServiceProvider.GetRequiredService<ShopDbContext>().Users.SingleAsync(u => u.Email == "hash@shop.rs");
        Assert.StartsWith("AQAAAA", user.PasswordHash); // PBKDF2 format v3, salted
    }

    [Fact]
    public async Task AccountLocksAfterRepeatedFailures()
    {
        await Register("lock@shop.rs", "Secret1!");
        for (var i = 0; i < 3; i++)
        {
            Assert.Equal(HttpStatusCode.Unauthorized, (await Login("lock@shop.rs", "wrong")).StatusCode);
        }
        var correctPassword = await Login("lock@shop.rs", "Secret1!");
        Assert.Equal(HttpStatusCode.Unauthorized, correctPassword.StatusCode);
        Assert.Equal("LockedOut", (await correctPassword.JsonAsync()).GetProperty("detail").GetString());
    }
}
