// VERDICT | T13 Compile-time features | BETTER: C#
// WHY: partial classes, source generators, #if and [Conditional]; Java has no equivalents (subclass hooks, runtime flags).

namespace Shop.T13_CompileTime;

/// <summary>The hand-written half: same class, another file, no inheritance needed.</summary>
public partial class ProductForm
{
    partial void OnValidating(List<string> errors)
    {
        if (Price <= 0)
        {
            errors.Add("Price must be positive");
        }
    }
}
