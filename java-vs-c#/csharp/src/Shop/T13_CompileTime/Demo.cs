// VERDICT | T13 Compile-time features | BETTER: C#
// WHY: partial classes, source generators, #if and [Conditional]; Java has no equivalents (subclass hooks, runtime flags).

namespace Shop.T13_CompileTime;

public static class Demo
{
    public static void Run()
    {
        Console.WriteLine($"B12 valid? {SkuValidator.IsValid("B12")}, b-12 valid? {SkuValidator.IsValid("b-12")}");
        Console.WriteLine($"Empty form errors: [{string.Join(", ", new ProductForm().Validate())}]");
        Console.WriteLine($"Build mode: {BuildInfo.Mode}");
        BuildInfo.Trace("demo 13 ran");
        Console.WriteLine($"Trace: [{string.Join(", ", BuildInfo.Traced)}]");

        var settings = DynamicSettings.Defaults();
        int next = DynamicSettings.NextPageSize(settings);
        Console.WriteLine($"Theme {settings.Theme}, next page size {next}");
    }
}
