// VERDICT | T12 Security | BETTER: SPRING
// WHY: Spring Security ships HTTP Basic, in-memory users and @PreAuthorize expressions over method arguments; ASP.NET needs a custom Basic handler and an authorization handler for the same rules.

using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Shop.Api.T01_Endpoints;
using Shop.Api.T07_Transactions;

namespace Shop.Api.T12_Security;

/// <summary>"Admin, and at most N units" - a rule over the method argument needs a requirement + handler pair.</summary>
public sealed class RestockRequirement(int maxQuantity) : IAuthorizationRequirement
{
    public int MaxQuantity => maxQuantity;
}

public sealed class RestockHandler : AuthorizationHandler<RestockRequirement, int>
{
    protected override Task HandleRequirementAsync(AuthorizationHandlerContext context, RestockRequirement requirement, int quantity)
    {
        if (context.User.IsInRole("Admin") && quantity <= requirement.MaxQuantity)
        {
            context.Succeed(requirement);
        }
        return Task.CompletedTask;
    }
}

public class ForbiddenException() : Exception("Access denied");

public sealed class InventoryService(ShopDbContext db, IAuthorizationService authorization)
{
    /// <summary>
    /// [Authorize] works on endpoints only; a check inside a service method has to call
    /// IAuthorizationService by hand (Spring: @PreAuthorize("hasRole('ADMIN') and #quantity &lt;= 1000")).
    /// </summary>
    public async Task<ProductDto> RestockAsync(ClaimsPrincipal user, string sku, int quantity)
    {
        var decision = await authorization.AuthorizeAsync(user, quantity, SecurityModule.RestockPolicy);
        if (!decision.Succeeded)
        {
            throw new ForbiddenException();
        }
        var product = await db.Products.FindAsync(sku) ?? throw new UnknownProductException(sku);
        product.AddStock(quantity);
        await db.SaveChangesAsync();
        return ProductDto.From(product);
    }
}
