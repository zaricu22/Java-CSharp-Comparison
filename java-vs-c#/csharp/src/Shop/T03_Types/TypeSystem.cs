// VERDICT | T03 Type system & generics | BETTER: C#
// WHY: reified generics (typeof(T), new T(), List<int> without boxing) and generic math; Java has erasure and Integer boxing traps.

using System.Numerics;

namespace Shop.T03_Types;

public static class TypeSystem
{
    /// <summary>Unified type system: int is System.Int32, a struct with methods.</summary>
    public static string DescribeStock(int quantity) =>
        $"{quantity.ToString()} units (0x{quantity.ToString("x")})";

    /// <summary>List&lt;int&gt; stores raw ints: no boxing, and a null element is impossible.</summary>
    public static int TotalUnits(List<int> quantities) => quantities.Sum();

    /// <summary>Generic math (C# 11): one method for int, long, decimal, double...</summary>
    public static T Sum<T>(IEnumerable<T> values) where T : INumber<T>
    {
        var total = T.Zero;
        foreach (var v in values)
        {
            total += v;
        }
        return total;
    }

    /// <summary>Reified generics: the runtime knows List&lt;string&gt; from List&lt;int&gt;, even when empty.</summary>
    public static bool IsListOfStrings(object o) => o is List<string>;
}

/// <summary>T is real at runtime: typeof(T) and new T() just work - no Class token or factory.</summary>
public sealed class Repository<T> where T : new()
{
    private readonly List<T> _items = [];

    public string EntityName => typeof(T).Name;

    public T CreateNew()
    {
        var item = new T();
        _items.Add(item);
        return item;
    }

    public int Count => _items.Count;
}

public class CartDraft
{
    public string Note { get; set; } = "new cart";
}
