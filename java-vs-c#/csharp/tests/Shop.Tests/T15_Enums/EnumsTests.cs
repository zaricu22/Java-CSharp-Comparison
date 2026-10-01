// VERDICT | T15 Enums | BETTER: JAVA
// WHY: enum constants carry fields, own method bodies and implement interfaces; C# enums are plain ints ((Enum)42 is legal).

namespace Shop.T15_Enums;

public class EnumsTests
{
    [Fact]
    public void EachMemberHasItsOwnCost()
    {
        Assert.Equal(4.99m, ShippingMethod.Standard.Cost(30m));
        Assert.Equal(0m, ShippingMethod.Standard.Cost(80m));
        Assert.Equal(9.99m, ShippingMethod.Express.Cost(80m));
        Assert.Equal(0m, ShippingMethod.Pickup.Cost(30m));
    }

    [Fact]
    public void MembersGetDataFromExtensions()
    {
        Assert.Equal("Express delivery", ShippingMethod.Express.Label);
        Assert.Equal(new DateOnly(2026, 3, 6), ShippingMethod.Standard.Eta(new DateOnly(2026, 3, 1)));
    }

    [Fact]
    public void EnumNeedsAnAdapterForInterfaces()
    {
        IPricingRule rule = new ShippingRule(ShippingMethod.Standard);
        Assert.Equal(34.99m, ShippingReport.TotalWith(rule, 30m));
    }

    [Fact]
    public void QuotesCoverAllMembers()
    {
        var quotes = ShippingReport.Quotes(30m);
        Assert.Equal([ShippingMethod.Standard, ShippingMethod.Express, ShippingMethod.Pickup], quotes.Keys);
        Assert.Contains(ShippingMethod.Express, ShippingReport.HomeDelivery);
    }

    [Fact]
    public void InvalidValuesCanExist()
    {
        Assert.Throws<ArgumentException>(() => Enum.Parse<ShippingMethod>("Drone"));

        var drone = (ShippingMethod)42;                         // compiles and runs
        var parsed = Enum.Parse<ShippingMethod>("42");          // also "succeeds"
        Assert.Equal(drone, parsed);
        Assert.False(Enum.IsDefined(drone));
        Assert.Throws<ArgumentOutOfRangeException>(() => drone.Label); // caught only by the "_" arm
    }
}
