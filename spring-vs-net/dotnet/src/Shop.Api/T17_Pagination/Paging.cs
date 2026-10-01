// VERDICT | T17 Pagination & sorting | BETTER: SPRING
// WHY: a Pageable parameter gives page/size/sort binding, defaults, a max page size and a Page with totals from the repository; ASP.NET writes Skip/Take, the count query, sort parsing and the page DTO by hand.

using Microsoft.EntityFrameworkCore;
using Shop.Api.T01_Endpoints;

namespace Shop.Api.T17_Pagination;

public record PageInfo(int Size, int Number, int TotalElements, int TotalPages);

public record PageResult<T>(IReadOnlyList<T> Content, PageInfo Page);

/// <summary>
/// Everything Spring Data's Pageable does, written by hand: parameter parsing, a whitelist of sortable
/// columns, defaults, a maximum page size, the count query, Skip/Take and the response shape.
/// </summary>
public static class Paging
{
    public const int DefaultSize = 20;
    public const int MaxSize = 2000;

    public class InvalidSortException(string sort) : Exception($"Cannot sort by '{sort}'");

    public static IQueryable<Product> SortBy(this IQueryable<Product> products, string sort)
    {
        var parts = sort.Split(',', 2, StringSplitOptions.TrimEntries);
        var descending = parts.Length == 2 && parts[1].Equals("desc", StringComparison.OrdinalIgnoreCase);
        return parts[0].ToLowerInvariant() switch
        {
            "sku" => descending ? products.OrderByDescending(p => p.Sku) : products.OrderBy(p => p.Sku),
            "name" => descending ? products.OrderByDescending(p => p.Name) : products.OrderBy(p => p.Name),
            "price" => descending ? products.OrderByDescending(p => p.Price) : products.OrderBy(p => p.Price),
            "stock" => descending ? products.OrderByDescending(p => p.Stock) : products.OrderBy(p => p.Stock),
            _ => throw new InvalidSortException(sort),
        };
    }

    public static async Task<PageResult<ProductDto>> ToPageAsync(this IQueryable<Product> products, int page, int size)
    {
        size = Math.Clamp(size, 1, MaxSize);
        page = Math.Max(page, 0);
        var total = await products.CountAsync();
        var items = await products.Skip(page * size).Take(size).AsNoTracking().ToListAsync();
        return new PageResult<ProductDto>(
            items.Select(ProductDto.From).ToList(),
            new PageInfo(size, page, total, (int)Math.Ceiling(total / (double)size)));
    }

    public static IEndpointRouteBuilder MapPagedProducts(this IEndpointRouteBuilder app)
    {
        app.MapGet("/api/products/paged", async (Category? category, ShopDbContext db, int page = 0, int size = DefaultSize, string sort = "sku") =>
        {
            try
            {
                IQueryable<Product> query = db.Products;
                if (category is not null)
                {
                    query = query.Where(p => p.Category == category);
                }
                return Results.Ok(await query.SortBy(sort).ToPageAsync(page, size));
            }
            catch (InvalidSortException e)
            {
                return Results.Problem(statusCode: StatusCodes.Status400BadRequest, detail: e.Message);
            }
        });
        return app;
    }
}
