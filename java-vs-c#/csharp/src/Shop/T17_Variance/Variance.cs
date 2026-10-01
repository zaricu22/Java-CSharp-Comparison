// VERDICT | T17 Generic variance | BETTER: JAVA
// WHY: use-site `? extends` / `? super` works on any type; C# has no lower bound, so consumers need a delegate workaround.

namespace Shop.T17_Variance;

public interface ISellable
{
    string Title { get; }

    decimal Price { get; }
}

public record Book(string Title, decimal Price, int Pages) : ISellable;

public record Ebook(string Title, decimal Price, double SizeMb) : ISellable;

/// <summary>
/// C# has declaration-site variance only: "out T" on IEnumerable/IReadOnlyList, "in T" on
/// IComparer/Action. Mutable List&lt;T&gt;/ICollection&lt;T&gt; are invariant, and there is no
/// lower-bound ("super") constraint - some Java signatures need a workaround.
/// </summary>
public static class Variance
{
    /// <summary>Producer: works because IEnumerable&lt;out T&gt; is covariant.</summary>
    public static decimal Total(IEnumerable<ISellable> items) => items.Sum(i => i.Price);

    /// <summary>
    /// Consumer: "ICollection of Ebook or any supertype" cannot be declared.
    /// Workaround: take a contravariant delegate and let the caller pass list.Add.
    /// </summary>
    public static void AddFreebie(Action<Ebook> add) => add(new Ebook("Java Cheat Sheet", 0m, 1.2));

    /// <summary>PECS via a second type parameter: TSource must derive from TTarget.</summary>
    public static void CopyMatching<TSource, TTarget>(IEnumerable<TSource> source, ICollection<TTarget> target, Func<TSource, bool> filter)
        where TSource : TTarget
    {
        foreach (var item in source)
        {
            if (filter(item))
            {
                target.Add(item);
            }
        }
    }

    /// <summary>IComparer&lt;in T&gt; is contravariant, so a comparer of ISellable sorts Ebooks.</summary>
    public static T Cheapest<T>(IEnumerable<T> items, IComparer<T> byPrice) => items.Min(byPrice)!;
}
