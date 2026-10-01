// VERDICT | T04 Validation & ProblemDetails | BETTER: ASP.NET
// WHY: AddValidation() + AddProblemDetails() return RFC 9457 errors per field out of the box; Spring returns ProblemDetail too but needs an advice to list field errors.

using System.Text;

namespace Shop.Api.Tests.T04_Validation;

public class ValidationTests
{
    private static StringContent Json(string json) => new(json, Encoding.UTF8, "application/json");

    [Fact]
    public async Task InvalidBodyListsEveryFieldError()
    {
        var client = new ShopApiFactory().CreateClient();
        var response = await client.PostAsync("/api/products", Json("""{"sku": "bad", "name": "", "price": 0, "stock": -1}"""));

        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
        Assert.Equal("application/problem+json", response.Content.Headers.ContentType?.MediaType);
        var errors = (await response.JsonAsync()).GetProperty("errors").EnumerateObject()
            .ToDictionary(e => e.Name.ToLowerInvariant(), e => e.Value[0].GetString());
        Assert.Equal(["category", "name", "price", "sku", "stock"], errors.Keys.Order());
        Assert.Equal("must look like B12", errors["sku"]);
    }

    [Fact]
    public async Task ValidBodyCreatesProduct()
    {
        var client = new ShopApiFactory().CreateClient();
        var response = await client.PostAsync("/api/products",
            Json("""{"sku": "Z1", "name": "Tea", "category": "Grocery", "price": 4.20, "stock": 10}"""));

        Assert.Equal(HttpStatusCode.Created, response.StatusCode);
        Assert.Equal("/api/products/Z1", response.Headers.Location?.ToString());
        Assert.Equal("Tea", (await response.JsonAsync()).GetProperty("name").GetString());
    }

    [Fact]
    public async Task DuplicateSkuIsConflict()
    {
        var client = new ShopApiFactory().CreateClient();
        var response = await client.PostAsync("/api/products",
            Json("""{"sku": "B1", "name": "Copy", "category": "Books", "price": 1, "stock": 1}"""));

        Assert.Equal(HttpStatusCode.Conflict, response.StatusCode);
        Assert.Equal("SKU B1 already exists", (await response.JsonAsync()).GetProperty("detail").GetString());
    }
}
