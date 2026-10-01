// VERDICT | T13 Compile-time features | BETTER: C#
// WHY: partial classes, source generators, #if and [Conditional]; Java has no equivalents (subclass hooks, runtime flags).

using System.Text.RegularExpressions;

namespace Shop.T13_CompileTime;

/// <summary>
/// Partial method + source generator: the compiler generates the regex matcher code at
/// build time (no runtime parsing, trimming/AOT friendly). Partial classes are what let
/// generated code and hand-written code share one type.
/// </summary>
public static partial class SkuValidator
{
    [GeneratedRegex(@"^[A-Z]\d{1,3}$")]
    private static partial Regex SkuPattern();

    public static bool IsValid(string sku) => SkuPattern().IsMatch(sku);
}
