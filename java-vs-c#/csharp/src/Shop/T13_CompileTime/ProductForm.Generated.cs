// VERDICT | T13 Compile-time features | BETTER: C#
// WHY: partial classes, source generators, #if and [Conditional]; Java has no equivalents (subclass hooks, runtime flags).

// Imagine a designer or source generator produced this half of the class.
namespace Shop.T13_CompileTime;

public partial class ProductForm
{
    public string Name { get; set; } = "";

    public decimal Price { get; set; }

    /// <summary>Optional hook: if no other part implements it, the call below is removed by the compiler.</summary>
    partial void OnValidating(List<string> errors);

    public IReadOnlyList<string> Validate()
    {
        var errors = new List<string>();
        if (string.IsNullOrWhiteSpace(Name))
        {
            errors.Add("Name is required");
        }
        OnValidating(errors);
        return errors;
    }
}
