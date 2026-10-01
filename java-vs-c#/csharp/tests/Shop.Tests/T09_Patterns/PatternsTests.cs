// VERDICT | T09 Pattern matching | BETTER: TIE
// WHY: C# has relational, tuple and list patterns; Java checks switch exhaustiveness over sealed types at compile time.

using static Shop.T09_Patterns.PaymentResult;

namespace Shop.T09_Patterns;

public class PatternsTests
{
    [Fact]
    public void DescribePaymentResults()
    {
        Assert.Equal("Approved large payment TX-1", Patterns.Describe(new Approved("TX-1", 1500m)));
        Assert.Equal("Approved TX-2", Patterns.Describe(new Approved("TX-2", 20m)));
        Assert.Equal("Declined: expired card", Patterns.Describe(new Declined("expired card")));
        Assert.Equal("Retry in 30s", Patterns.Describe(new Pending(TimeSpan.FromSeconds(30))));
        Assert.Equal("Blocked", Patterns.Describe(new FraudSuspected(95)));
        Assert.Equal("Manual review", Patterns.Describe(new FraudSuspected(40)));
    }

    [Fact]
    public void ShippingCostByCountryAndWeight()
    {
        Assert.Equal(2.50m, Patterns.ShippingCost("RS", 0.5));
        Assert.Equal(5.00m, Patterns.ShippingCost("RS", 3));
        Assert.Equal(12.00m, Patterns.ShippingCost("RS", 25));
        Assert.Equal(9.90m, Patterns.ShippingCost("AT", 5));
        Assert.Equal(19.90m, Patterns.ShippingCost("DE", 6));
        Assert.Equal(29.90m, Patterns.ShippingCost("US", 1));
    }

    [Fact]
    public void CartSummaryByShape()
    {
        Assert.Equal("Cart is empty", Patterns.CartSummary([]));
        Assert.Equal("1 item: Coffee", Patterns.CartSummary(["Coffee"]));
        Assert.Equal("2 items: Coffee, Monitor", Patterns.CartSummary(["Coffee", "Monitor"]));
        Assert.Equal("4 items: A ... D", Patterns.CartSummary(["A", "B", "C", "D"]));
    }
}
