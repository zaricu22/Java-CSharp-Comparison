// VERDICT | T02 Strings | BETTER: C#
// WHY: interpolation puts each value where it is printed ({x,-12} {y:F2}); Java only has positional %s placeholders.

namespace Shop.T02_Strings;

/// <summary>
/// Interpolation puts each value exactly where it is printed, with alignment ({x,-12})
/// and format ({x:F2}) inline. Raw string literals ($""") handle multi-line text and quotes.
/// </summary>
public static class InvoiceFormatter
{
    public static string Line(OrderLine line) =>
        $"{line.Product.Name,-12} x{line.Quantity,2} {line.Total,8:F2}";

    public static string Greeting(string name, int orderCount) =>
        $"Hello {name}, you have {orderCount} order{(orderCount == 1 ? "" : "s")}.";

    public static string Invoice(Order order) => $"""
        INVOICE #{order.Id}
        Customer: {order.Customer.Name} ({order.Customer.City})
        Date:     {order.Date:yyyy-MM-dd}
        {string.Join("\n", order.Lines.Select(Line))}
        TOTAL:    {order.Total:F2}
        """;

    /// <summary>$$ means "{{x}}" interpolates and a single { is literal - ideal for JSON.</summary>
    public static string ToJson(Order order) =>
        $$"""{"id": {{order.Id}}, "customer": "{{order.Customer.Name}}", "total": {{order.Total:F2}}}""";
}
