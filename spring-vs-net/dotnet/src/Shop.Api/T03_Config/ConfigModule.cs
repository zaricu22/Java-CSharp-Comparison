// VERDICT | T03 Configuration & profiles | BETTER: SPRING
// WHY: @Profile and @ConditionalOnProperty swap beans declaratively; ASP.NET binds options just as well, but conditional registration is hand-written if/else.

using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using Shop.Api.T05_Repositories;

namespace Shop.Api.T03_Config;

public interface INotificationSender
{
    string Channel { get; }
}

internal sealed class LogNotificationSender : INotificationSender
{
    public string Channel => "log";
}

internal sealed class SmtpNotificationSender : INotificationSender
{
    public string Channel => "smtp";
}

/// <summary>Only registered when Shop:Features:Recommendations is true (a feature flag).</summary>
public sealed class RecommendationService(ShopDbContext db)
{
    public async Task<List<string>> RecommendForAsync(string sku)
    {
        if (await db.Products.FindAsync(sku) is not { } product)
        {
            return [];
        }
        return await db.Products.InCategory(product.Category).Where(p => p.Sku != sku).Select(p => p.Name).ToListAsync();
    }
}

public record ConfigView(string Currency, decimal FreeShippingOver, string NotificationChannel, bool Recommendations);

public static class ConfigModule
{
    public static WebApplicationBuilder AddShopConfiguration(this WebApplicationBuilder builder)
    {
        builder.Services.AddOptions<ShopOptions>()
            .Bind(builder.Configuration.GetSection(ShopOptions.Section))
            .ValidateDataAnnotations()
            .ValidateOnStart();

        // Spring: @Profile("prod") / @Profile("!prod") on the two classes. Here: an explicit if/else.
        if (builder.Environment.IsProduction())
        {
            builder.Services.AddSingleton<INotificationSender, SmtpNotificationSender>();
        }
        else
        {
            builder.Services.AddSingleton<INotificationSender, LogNotificationSender>();
        }

        // Spring: @ConditionalOnProperty on the class. Here: read the flag while registering.
        if (builder.Configuration.GetValue<bool>("Shop:Features:Recommendations"))
        {
            builder.Services.AddScoped<RecommendationService>();
        }

        return builder;
    }

    public static IEndpointRouteBuilder MapConfigEndpoints(this IEndpointRouteBuilder app)
    {
        app.MapGet("/api/config", (IOptions<ShopOptions> options, INotificationSender sender, IServiceProvider services) =>
            new ConfigView(options.Value.Currency, options.Value.FreeShippingOver, sender.Channel,
                services.GetService<RecommendationService>() is not null));

        app.MapGet("/api/recommendations/{sku}", async (string sku, IServiceProvider services) =>
            services.GetService<RecommendationService>() is { } recommendations
                ? Results.Ok(await recommendations.RecommendForAsync(sku))
                : Results.Problem(statusCode: StatusCodes.Status404NotFound, detail: "Recommendations are disabled"));

        return app;
    }
}
