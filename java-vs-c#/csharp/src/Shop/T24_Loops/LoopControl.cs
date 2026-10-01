// VERDICT | T24 Labeled break & continue | BETTER: JAVA
// WHY: `break search;` / `continue orders;` leave or skip an outer loop directly; C# needs goto, a flag variable or an extra method.

namespace Shop.T24_Loops;

/// <summary>
/// No labeled break/continue: leaving an outer loop from an inner one needs goto
/// (the official C# answer), a flag variable, or moving the loop into its own method.
/// (Both languages can often avoid nested loops with Any / anyMatch - these methods show
/// the loop form, which is still common in parsing, matrix and search code.)
/// </summary>
public static class LoopControl
{
    /// <summary>Stop everything at the first order line whose product is out of stock.</summary>
    public static string FirstUnavailable(IEnumerable<Order> orders, IReadOnlySet<string> outOfStock)
    {
        string? found = null;
        foreach (var order in orders)
        {
            foreach (var line in order.Lines)
            {
                if (outOfStock.Contains(line.Product.Sku))
                {
                    found = $"order {order.Id}: {line.Product.Name}";
                    goto done;
                }
            }
        }
    done:
        return found is null ? "all available" : $"Blocked {found}";
    }

    /// <summary>Skip a whole order as soon as one of its lines is out of stock - via a flag.</summary>
    public static List<int> ShippableOrders(IEnumerable<Order> orders, IReadOnlySet<string> outOfStock)
    {
        var ids = new List<int>();
        foreach (var order in orders)
        {
            var blocked = false;
            foreach (var line in order.Lines)
            {
                if (outOfStock.Contains(line.Product.Sku))
                {
                    blocked = true;
                    break;
                }
            }
            if (blocked)
            {
                continue;
            }
            ids.Add(order.Id);
        }
        return ids;
    }
}
