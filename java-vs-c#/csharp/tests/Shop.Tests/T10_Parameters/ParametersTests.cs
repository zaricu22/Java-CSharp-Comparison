// VERDICT | T10 Method parameters | BETTER: C#
// WHY: optional/named arguments, out/ref and tuple returns; Java needs parameter objects, result records and try/catch parsing.

namespace Shop.T10_Parameters;

public class ParametersTests
{
    [Fact]
    public void QuoteWithDefaults()
    {
        Assert.Equal("100.00 EUR", Parameters.Quote(100m));
    }

    [Fact]
    public void QuoteWithSomeOptions()
    {
        Assert.Equal("109.99 EUR", Parameters.Quote(100m, express: true));
        Assert.Equal("90.00 USD", Parameters.Quote(100m, currency: "USD", discountPercent: 10));
    }

    [Fact]
    public void MultipleReturnValues()
    {
        var (min, max) = Parameters.PriceRange(SampleData.Products);
        Assert.Equal(12.40m, min);
        Assert.Equal(249.00m, max);
    }

    [Fact]
    public void TryParseQuantity()
    {
        Assert.True(Parameters.TryParseQuantity(" 3 ", out var quantity));
        Assert.Equal(3, quantity);
        Assert.False(Parameters.TryParseQuantity("three", out _));
        Assert.False(Parameters.TryParseQuantity("0", out _));
    }

    [Fact]
    public void NormalizeSwapsReversedRange()
    {
        decimal min = 10m, max = 1m;
        Parameters.Normalize(ref min, ref max);
        Assert.Equal((1m, 10m), (min, max));
    }
}
