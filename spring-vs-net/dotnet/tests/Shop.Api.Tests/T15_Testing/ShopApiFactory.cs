// VERDICT | T15 Testing | BETTER: TIE
// WHY: Spring has test slices (@WebMvcTest, @DataJpaTest) and Mockito mocks concrete classes; ASP.NET's WebApplicationFactory runs the whole app in memory, fast and simple, with interface-based fakes.

using System.Text.Json;
using System.Text.Json.Serialization;
using Microsoft.AspNetCore.Mvc.Testing;

namespace Shop.Api.Tests.T15_Testing;

/// <summary>
/// Boots the real Program (all of it) on an in-memory TestServer - no ports, no sockets.
/// Every instance gets its own in-memory SQLite database, so tests that change data
/// create their own factory instead of sharing one (Spring: @DirtiesContext).
/// </summary>
public class ShopApiFactory : WebApplicationFactory<Program>
{
    public static readonly JsonSerializerOptions Json = new(JsonSerializerDefaults.Web)
    {
        Converters = { new JsonStringEnumConverter() },
    };
}

public static class JsonTestExtensions
{
    public static async Task<JsonElement> JsonAsync(this HttpResponseMessage response) =>
        (await response.Content.ReadFromJsonAsync<JsonElement>(ShopApiFactory.Json));

    public static async Task<JsonElement> GetJsonAsync(this HttpClient client, string url) =>
        await (await client.GetAsync(url)).JsonAsync();
}
