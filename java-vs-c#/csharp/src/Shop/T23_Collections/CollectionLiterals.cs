// VERDICT | T23 Collection expressions | BETTER: C#
// WHY: one [a, ..b, ..c] syntax builds arrays, lists, sets and immutable collections; Java mixes List.of, addAll and streams, and List.of hides immutability until add() throws.

using System.Collections.Immutable;

namespace Shop.T23_Collections;

/// <summary>
/// Collection expressions (C# 12): one [ ... ] syntax for arrays, lists, spans, sets and
/// immutable collections, with ".." to spread other sequences in. The target type decides
/// what gets built, so mutability is visible in the type, not hidden behind an interface.
/// </summary>
public static class CollectionLiterals
{
    /// <summary>Promo first, then best sellers, then new arrivals - one line.</summary>
    public static List<Product> Featured(Product promo, IEnumerable<Product> bestSellers, IEnumerable<Product> newArrivals) =>
        [promo, .. bestSellers, .. newArrivals];

    public static int[] MergeQuantities(int[] first, int[] second) => [.. first, .. second];

    /// <summary>A mutable list: callers may add their own tags.</summary>
    public static List<string> DefaultTags() => ["sale", "new"];

    /// <summary>The same syntax builds an immutable set (its Add returns a new set).</summary>
    public static readonly ImmutableHashSet<string> ShippingCountries = ["RS", "DE", "AT"];

    public static List<Order> NoOrders() => [];
}
