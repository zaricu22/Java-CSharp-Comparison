// VERDICT | T05 Operators & value types | BETTER: C#
// WHY: operator overloading, structs, decimal and unsigned types give `(price * qty + ship) * 0.9m`; Java chains methods and masks bytes.

namespace Shop.T05_Operators;

public class OperatorsTests
{
    [Fact]
    public void CheckoutTotalCombinesOperations()
    {
        var total = Checkout.Total(Money.Eur(35.50m), 2, Money.Eur(4.99m), 0.9m);
        Assert.Equal(Money.Eur(68.39m), total);
    }

    [Fact]
    public void ComparisonUsesOperators()
    {
        Assert.True(Money.Eur(71) > Money.Eur(70));
        Assert.True(Checkout.QualifiesForFreeShipping(Money.Eur(50)));
        Assert.False(Checkout.QualifiesForFreeShipping(Money.Eur(49.99m)));
    }

    [Fact]
    public void MixingCurrenciesFails()
    {
        Assert.Throws<ArgumentException>(() => Money.Eur(1) + new Money(1, "USD"));
    }

    [Fact]
    public void EqualityIgnoresScale()
    {
        Assert.Equal(2.5m, 2.50m);                         // decimal compares by value
        Assert.True(Money.Eur(2.5m) == Money.Eur(2.50m)); // so the record struct does too
    }

    [Fact]
    public void ArrayElementsStartAsDefaultValues()
    {
        var prices = new Money[3];
        Assert.Equal(0m, prices[0].Amount); // a real value, not null...
        Assert.Null(prices[0].Currency);    // ...but default(struct) skips constructor defaults - a C# trap
    }

    [Fact]
    public void UnsignedBytesNeedNoMasking()
    {
        Assert.Equal(256, PacketReader.Checksum([0xFF, 0x01]));
        Assert.Equal((ushort)65534, PacketReader.ReadUInt16([0xFF, 0xFE], 0));
    }
}
