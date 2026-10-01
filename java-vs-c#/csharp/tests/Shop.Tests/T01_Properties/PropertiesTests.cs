// VERDICT | T01 Properties & records | BETTER: C#
// WHY: properties (`field`, `required`, `init`) and `with` replace Java's hand-written getters/setters and manual record copies.

namespace Shop.T01_Properties;

public class PropertiesTests
{
    private static CustomerAccount Ana() =>
        new() { Id = 1, Email = "ana@shop.rs", FirstName = "Ana", LastName = "Jovanovic" };

    [Fact]
    public void FullNameIsComputedFromParts()
    {
        var ana = Ana();
        ana.LastName = "Petrovic";
        Assert.Equal("Ana Petrovic", ana.FullName);
    }

    [Fact]
    public void InvalidEmailIsRejectedOnCreateAndOnChange()
    {
        Assert.Throws<ArgumentException>(() => new CustomerAccount { Id = 2, Email = "nope", FirstName = "A", LastName = "B" });
        var ana = Ana();
        Assert.Throws<ArgumentException>(() => ana.Email = "still-nope");
        Assert.Equal("ana@shop.rs", ana.Email);
    }

    [Fact]
    public void LoyaltyPointsChangeOnlyThroughMethod()
    {
        var ana = Ana();
        ana.AddPoints(100);
        ana.AddPoints(50);
        // ana.LoyaltyPoints = 5;  -> compile error CS0272: the setter is private
        Assert.Equal(150, ana.LoyaltyPoints);
    }

    [Fact]
    public void RecordCopyChangesOnlyThePrice()
    {
        var discounted = Pricing.Discounted(SampleData.CleanCode, 10);
        Assert.Equal(31.95m, discounted.Price);
        Assert.Equal(SampleData.CleanCode.Name, discounted.Name);
        Assert.Equal(35.50m, SampleData.CleanCode.Price); // original untouched
    }
}
