// VERDICT | T03 Configuration & profiles | BETTER: SPRING
// WHY: @Profile and @ConditionalOnProperty swap beans declaratively; ASP.NET binds options just as well, but conditional registration is hand-written if/else.

using System.ComponentModel.DataAnnotations;

namespace Shop.Api.T03_Config;

/// <summary>
/// Typed, validated settings bound from the "Shop" section of appsettings.json
/// (and overridden by appsettings.{Environment}.json). Invalid values stop the app at startup.
/// </summary>
public sealed class ShopOptions
{
    public const string Section = "Shop";

    [Required]
    public string Currency { get; set; } = "";

    [Range(typeof(decimal), "0", "1000000")]
    public decimal FreeShippingOver { get; set; }

    public FeatureOptions Features { get; set; } = new();

    public JobOptions Jobs { get; set; } = new();

    public RateLimitOptions RateLimit { get; set; } = new();
}

public sealed class FeatureOptions
{
    public bool Recommendations { get; set; }
}

public sealed class JobOptions
{
    public TimeSpan CleanupInterval { get; set; } = TimeSpan.FromHours(1);
}

public sealed class RateLimitOptions
{
    public int Permits { get; set; } = 3;
    public TimeSpan Window { get; set; } = TimeSpan.FromSeconds(10);
}
