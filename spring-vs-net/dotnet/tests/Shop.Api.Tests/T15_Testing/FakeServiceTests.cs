// VERDICT | T15 Testing | BETTER: TIE
// WHY: Spring has test slices (@WebMvcTest, @DataJpaTest) and Mockito mocks concrete classes; ASP.NET's WebApplicationFactory runs the whole app in memory, fast and simple, with interface-based fakes.

using Microsoft.AspNetCore.TestHost;
using Shop.Api.T01_Endpoints;
using Shop.Api.T04_Validation;

namespace Shop.Api.Tests.T15_Testing;

/// <summary>
/// No slices: the whole app starts, and ConfigureTestServices swaps one registration.
/// No mocking library ships with .NET, so the fake is a hand-written class implementing the
/// interface (Moq / NSubstitute are third-party and also need an interface or virtual methods).
/// </summary>
public class FakeServiceTests
{
    private sealed class FakeCatalogService : ICatalogService
    {
        public Task<ProductDto?> FindAsync(string sku) =>
            Task.FromResult(sku == "Q1" ? new ProductDto("Q1", "Faked", Category.Books, 9.99m, 1) : null);

        public Task<List<ProductDto>> ListAsync(Category? category) => Task.FromResult(new List<ProductDto>());
        public Task<RegisterResult> RegisterAsync(NewProduct request) => throw new NotSupportedException();
        public Task<List<ProductDto>> SearchAsync(string text) => Task.FromResult(new List<ProductDto>());
    }

    private static HttpClient ClientWithFake() =>
        new ShopApiFactory()
            .WithWebHostBuilder(host => host.ConfigureTestServices(services => services.AddScoped<ICatalogService, FakeCatalogService>()))
            .CreateClient();

    [Fact]
    public async Task EndpointUsesTheFakeService()
    {
        var product = await ClientWithFake().GetFromJsonAsync<ProductDto>("/api/products/Q1", ShopApiFactory.Json);
        Assert.Equal("Faked", product!.Name);
    }

    [Fact]
    public async Task MissingProductFromFakeIs404()
    {
        var response = await ClientWithFake().GetAsync("/api/products/Q2");
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }
}
