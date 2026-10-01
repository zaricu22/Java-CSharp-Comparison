// VERDICT | T18 Null safety | BETTER: C#
// WHY: nullable reference types (checked by the compiler) plus ?. ?? ??=; Java has only Optional/null checks and NPEs at runtime.

namespace Shop.T18_NullSafety;

public class NullSafetyTests
{
    private readonly CustomerProfile _ana = new("Ana", new Address("Belgrade", "Knez Mihailova 1"), null);
    private readonly CustomerProfile _marko = new("Marko", null, "+381 60 123");
    private readonly CustomerProfile _ghost = new("Ghost", null, null);

    [Fact]
    public void SafeNavigationWithDefault()
    {
        Assert.Equal("Belgrade", NullSafety.CityOf(_ana));
        Assert.Equal("unknown", NullSafety.CityOf(_marko));
        Assert.Equal("unknown", NullSafety.CityOf(null));
    }

    [Fact]
    public void OptionalLengthAndFallbackChain()
    {
        Assert.Equal(0, NullSafety.PhoneLength(_ana));
        Assert.Equal(11, NullSafety.PhoneLength(_marko));
        Assert.Equal("Knez Mihailova 1", NullSafety.Contact(_ana));
        Assert.Equal("+381 60 123", NullSafety.Contact(_marko));
        Assert.Equal("no contact", NullSafety.Contact(_ghost));
    }

    [Fact]
    public void UnsafeDereferenceDoesNotCompile()
    {
        // _marko.Address.City  -> error CS8602 (nullable warnings are errors in this solution).
        // The compiler forces the check, and after it the flow analysis knows Address is not null:
        var city = _marko.Address is { } address ? address.City : "none";
        Assert.Equal("none", city);
    }

    [Fact]
    public void FindReturnsNullableReference()
    {
        Assert.Same(_ana, NullSafety.Find([_ana, _marko], "Ana"));
        Assert.Null(NullSafety.Find([_ana, _marko], "Jelena"));
    }

    [Fact]
    public void LazyInitialisationHappensOnce()
    {
        Assert.Same(NullSafety.AuditLog, NullSafety.AuditLog);
    }
}
