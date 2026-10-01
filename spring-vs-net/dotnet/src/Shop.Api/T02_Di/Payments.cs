// VERDICT | T02 Dependency injection | BETTER: SPRING
// WHY: component scanning, Map<name, bean> injection and scoped proxies (request bean inside a singleton); ASP.NET registers everything by hand (easier to trace).

namespace Shop.Api.T02_Di;

public interface IPaymentProvider
{
    decimal Fee(decimal amount);
}

internal sealed class CardPayment : IPaymentProvider
{
    public decimal Fee(decimal amount) => Math.Round(amount * 0.015m + 0.25m, 2);
}

internal sealed class PayPalPayment : IPaymentProvider
{
    public decimal Fee(decimal amount) => Math.Round(amount * 0.029m + 0.35m, 2);
}

internal sealed class BankTransferPayment : IPaymentProvider
{
    public decimal Fee(decimal amount) => 0m;
}

public sealed class PaymentService(IServiceProvider services)
{
    /// <summary>Keyed services cannot be listed with their keys, so the key list is kept by hand.</summary>
    public static readonly string[] Methods = ["bank", "card", "paypal"];

    public decimal? Fee(string method, decimal amount) => services.GetKeyedService<IPaymentProvider>(method)?.Fee(amount);
}

/// <summary>One instance per HTTP request (Spring: @RequestScope).</summary>
public sealed class RequestInfo
{
    public string Id { get; } = Guid.NewGuid().ToString();
}

/// <summary>
/// Must be SCOPED as well: a singleton depending on a scoped service is rejected at startup
/// ("Cannot consume scoped service ... from singleton"). Spring solves this with a scoped proxy.
/// </summary>
public sealed class RequestLogger(RequestInfo requestInfo)
{
    public string CurrentRequestId => requestInfo.Id;
}

public static class DiModule
{
    /// <summary>Every registration is explicit - nothing is discovered by scanning.</summary>
    public static IServiceCollection AddPayments(this IServiceCollection services)
    {
        services.AddKeyedSingleton<IPaymentProvider, CardPayment>("card");
        services.AddKeyedSingleton<IPaymentProvider, PayPalPayment>("paypal");
        services.AddKeyedSingleton<IPaymentProvider, BankTransferPayment>("bank");
        services.AddSingleton<PaymentService>();
        services.AddScoped<RequestInfo>();
        services.AddScoped<RequestLogger>();
        return services;
    }

    public static IEndpointRouteBuilder MapDiEndpoints(this IEndpointRouteBuilder app)
    {
        app.MapGet("/api/payments", () => PaymentService.Methods);

        app.MapGet("/api/payments/{method}/fee", (string method, decimal amount, PaymentService payments) =>
            payments.Fee(method, amount) is { } fee
                ? Results.Ok(new { method, fee })
                : Results.Problem(statusCode: StatusCodes.Status404NotFound, detail: $"Unknown payment method {method}"));

        app.MapGet("/api/di/request-ids", (RequestInfo requestInfo, RequestLogger logger) =>
            new { controller = requestInfo.Id, scopedLogger = logger.CurrentRequestId });

        return app;
    }
}
