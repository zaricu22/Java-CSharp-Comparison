// VERDICT | Domain model | BETTER: ASP.NET
// WHY: EF Core maps plain classes by convention (properties, no annotations); JPA needs @Entity/@Id, a protected no-arg constructor and getters.

using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.Data.Sqlite;
using Microsoft.EntityFrameworkCore;

namespace Shop.Api.Domain;

/// <summary>IdentityDbContext adds the ASP.NET Identity tables (users, roles, claims, logins, tokens) for T20.</summary>
public sealed class ShopDbContext(DbContextOptions<ShopDbContext> options) : IdentityDbContext<IdentityUser>(options)
{
    public DbSet<Product> Products => Set<Product>();
    public DbSet<Order> Orders => Set<Order>();
    public DbSet<OrderLine> OrderLines => Set<OrderLine>();

    protected override void ConfigureConventions(ModelConfigurationBuilder configuration)
    {
        configuration.Properties<Category>().HaveConversion<string>();
        // SQLite has no DECIMAL type and EF cannot sort/sum decimals there, so they are stored as REAL.
        // With SQL Server or PostgreSQL this line is not needed. (Java's H2 has real DECIMAL columns.)
        configuration.Properties<decimal>().HaveConversion<double>();
    }

    protected override void OnModelCreating(ModelBuilder model)
    {
        base.OnModelCreating(model); // Identity tables
        model.Entity<Product>().HasKey(p => p.Sku);
        model.Entity<OrderLine>().HasOne(l => l.Product).WithMany().HasForeignKey(l => l.ProductSku);
    }
}

public static class PersistenceModule
{
    /// <summary>
    /// In-memory SQLite (like H2 on the Spring side). Every DbContext opens its OWN connection to a named,
    /// shared-cache in-memory database; one extra connection is kept open so the database lives as long as
    /// the app. (Sharing one SqliteConnection between concurrent requests is not thread-safe: the load test
    /// found ~40% HTTP 500s with that setup. H2 on the Spring side sits behind a HikariCP connection pool.)
    /// The GUID gives every app instance - and every test factory - its own database.
    /// </summary>
    public static IServiceCollection AddShopPersistence(this IServiceCollection services)
    {
        var connectionString = $"Data Source=shop-{Guid.NewGuid():N};Mode=Memory;Cache=Shared";
        services.AddSingleton(_ =>
        {
            var keepAlive = new SqliteConnection(connectionString);
            keepAlive.Open();
            return keepAlive;
        });
        services.AddDbContext<ShopDbContext>((sp, options) =>
        {
            sp.GetRequiredService<SqliteConnection>(); // make sure the keep-alive connection exists first
            options.UseSqlite(connectionString);
        });
        return services;
    }
}
