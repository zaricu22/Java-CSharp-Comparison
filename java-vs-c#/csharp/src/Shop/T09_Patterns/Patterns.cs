// VERDICT | T09 Pattern matching | BETTER: TIE
// WHY: C# has relational, tuple and list patterns; Java checks switch exhaustiveness over sealed types at compile time.

using System.Diagnostics;
using static Shop.T09_Patterns.PaymentResult;

namespace Shop.T09_Patterns;

/// <summary>
/// The closest C# gets to a sealed hierarchy: a private constructor means only nested types
/// can derive. The compiler still does not treat the set as closed, so switches need a "_" arm.
/// </summary>
public abstract record PaymentResult
{
    private PaymentResult() { }

    public sealed record Approved(string TransactionId, decimal Amount) : PaymentResult;

    public sealed record Declined(string Reason) : PaymentResult;

    public sealed record Pending(TimeSpan RetryAfter) : PaymentResult;

    public sealed record FraudSuspected(int Score) : PaymentResult;
}

public static class Patterns
{
    /// <summary>Property patterns with relational tests: { Amount: > 1000m }.</summary>
    public static string Describe(PaymentResult result) => result switch
    {
        Approved { Amount: > 1000m } a => $"Approved large payment {a.TransactionId}",
        Approved a => $"Approved {a.TransactionId}",
        Declined(var reason) => $"Declined: {reason}",
        Pending(var retryAfter) => $"Retry in {retryAfter.TotalSeconds}s",
        FraudSuspected { Score: >= 90 } => "Blocked",
        FraudSuspected => "Manual review",
        _ => throw new UnreachableException(), // required: a new subtype would silently land here
    };

    /// <summary>Tuple patterns + relational patterns + "or" patterns: a decision table.</summary>
    public static decimal ShippingCost(string country, double weightKg) => (country, weightKg) switch
    {
        ("RS", <= 1) => 2.50m,
        ("RS", <= 10) => 5.00m,
        ("RS", _) => 12.00m,
        ("DE" or "AT", <= 5) => 9.90m,
        ("DE" or "AT", _) => 19.90m,
        _ => 29.90m,
    };

    /// <summary>List patterns (C# 11) match on the shape of a collection.</summary>
    public static string CartSummary(IReadOnlyList<string> items) => items switch
    {
        [] => "Cart is empty",
        [var only] => $"1 item: {only}",
        [var first, var second] => $"2 items: {first}, {second}",
        [var first, .., var last] => $"{items.Count} items: {first} ... {last}",
    };
}
