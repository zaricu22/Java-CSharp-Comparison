// VERDICT | T17 Pagination & sorting | BETTER: SPRING
// WHY: a Pageable parameter gives page/size/sort binding, defaults, a max page size and a Page with totals from the repository; ASP.NET writes Skip/Take, the count query, sort parsing and the page DTO by hand.

using Shop.Api.T01_Endpoints;
using Shop.Api.T17_Pagination;

namespace Shop.Api.Tests.T17_Pagination;

public class PaginationTests(ShopApiFactory factory) : IClassFixture<ShopApiFactory>
{
    private readonly HttpClient _client = factory.CreateClient();

    private async Task<PageResult<ProductDto>> Page(string query) =>
        (await _client.GetFromJsonAsync<PageResult<ProductDto>>($"/api/products/paged{query}", ShopApiFactory.Json))!;

    [Fact]
    public async Task FirstPageSortedByPriceDescending()
    {
        var page = await Page("?page=0&size=2&sort=price,desc");
        Assert.Equal(["E2", "E1"], page.Content.Select(p => p.Sku));
        Assert.Equal(new PageInfo(Size: 2, Number: 0, TotalElements: 5, TotalPages: 3), page.Page);
    }

    [Fact]
    public async Task LastPageHasTheRemainder()
    {
        Assert.Equal(["G1"], (await Page("?page=2&size=2&sort=price,desc")).Content.Select(p => p.Sku));
    }

    [Fact]
    public async Task FilteredQueryIsPagedToo()
    {
        var page = await Page("?category=Books&sort=price,desc");
        Assert.Equal(["B2", "B1"], page.Content.Select(p => p.Sku));
        Assert.Equal(2, page.Page.TotalElements);
    }

    [Fact]
    public async Task DefaultsAndMaximumSizeApply()
    {
        var defaults = await Page("");
        Assert.Equal("B1", defaults.Content[0].Sku);
        Assert.Equal(20, defaults.Page.Size);
        Assert.Equal(2000, (await Page("?size=100000")).Page.Size);
    }

    [Fact]
    public async Task UnknownSortColumnIsRejected()
    {
        // The hand-written whitelist: only listed columns can be sorted on
        Assert.Equal(HttpStatusCode.BadRequest, (await _client.GetAsync("/api/products/paged?sort=secret")).StatusCode);
    }
}
