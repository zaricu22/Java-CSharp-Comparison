// VERDICT | T06 Complex queries | BETTER: ASP.NET
// WHY: LINQ is type-checked and refactor-safe (GroupBy/Sum/conditional Where); Spring uses JPQL strings or the verbose Criteria/Specification API with string attribute names.

using Shop.Api.T06_Queries;

namespace Shop.Api.Tests.T06_Queries;

public class ReportsTests(ShopApiFactory factory) : IClassFixture<ShopApiFactory>
{
    private readonly HttpClient _client = factory.CreateClient();

    [Fact]
    public async Task RevenueByCategoryBiggestFirst()
    {
        var revenue = await _client.GetFromJsonAsync<List<CategoryRevenue>>("/api/reports/revenue-by-category", ShopApiFactory.Json);
        Assert.Equal(
            [new(Category.Electronics, 518.97m), new(Category.Books, 113.00m), new CategoryRevenue(Category.Grocery, 99.20m)],
            revenue);
    }

    [Fact]
    public async Task TopCustomersSinceDate()
    {
        var top = await _client.GetFromJsonAsync<List<CustomerTotal>>("/api/reports/top-customers?since=2026-01-01&limit=2", ShopApiFactory.Json);
        Assert.Equal([new("Marko", 286.20m), new CustomerTotal("Ana", 222.99m)], top);
    }

    [Fact]
    public async Task DynamicFilters()
    {
        var all = await _client.GetFromJsonAsync<int[]>("/api/reports/orders");
        var books = await _client.GetFromJsonAsync<int[]>("/api/reports/orders?category=Books");
        var anaGrocery = await _client.GetFromJsonAsync<int[]>("/api/reports/orders?customer=Ana&category=Grocery");
        Assert.Equal([1, 2, 3, 4], all!);
        Assert.Equal([1, 4], books!);
        Assert.Equal([3], anaGrocery!);
    }
}
