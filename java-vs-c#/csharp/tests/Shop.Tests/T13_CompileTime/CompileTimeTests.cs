// VERDICT | T13 Compile-time features | BETTER: C#
// WHY: partial classes, source generators, #if and [Conditional]; Java has no equivalents (subclass hooks, runtime flags).

using Microsoft.CSharp.RuntimeBinder;

namespace Shop.T13_CompileTime;

public class CompileTimeTests
{
    [Fact]
    public void SkuValidation()
    {
        Assert.True(SkuValidator.IsValid("B1"));
        Assert.True(SkuValidator.IsValid("E123"));
        Assert.False(SkuValidator.IsValid("b1"));
        Assert.False(SkuValidator.IsValid("E1234"));
    }

    [Fact]
    public void GeneratedAndHandWrittenValidationCombine()
    {
        var form = new ProductForm();
        Assert.Equal(["Name is required", "Price must be positive"], form.Validate());
        form.Name = "Coffee";
        form.Price = 12.40m;
        Assert.Empty(form.Validate());
    }

    [Fact]
    public void BuildModeIsDebug()
    {
        // "dotnet test -c Release" would make this "Release" - chosen at build time, no code edit
        Assert.Equal("Debug", BuildInfo.Mode);
    }

    [Fact]
    public void TraceRecordsInDebug()
    {
        BuildInfo.Trace("from test");
        Assert.Contains("from test", BuildInfo.Traced);
    }

    [Fact]
    public void DynamicSettingsBindAtRuntime()
    {
        var settings = DynamicSettings.Defaults();
        int next = DynamicSettings.NextPageSize(settings);
        Assert.Equal(40, next);
        settings.PageSize = "twenty"; // nothing stops a wrong type going in...
        Assert.Throws<RuntimeBinderException>(() => (int)DynamicSettings.NextPageSize(settings)); // ...until it is read
    }
}
