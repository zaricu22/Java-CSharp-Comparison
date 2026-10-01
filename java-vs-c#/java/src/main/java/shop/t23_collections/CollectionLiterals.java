// VERDICT | T23 Collection expressions | BETTER: C#
// WHY: one [a, ..b, ..c] syntax builds arrays, lists, sets and immutable collections; Java mixes List.of, addAll and streams, and List.of hides immutability until add() throws.

package shop.t23_collections;

import shop.domain.Order;
import shop.domain.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Java has concise factories for fixed, immutable collections (List.of, Set.of, Map.of),
 * but no literal syntax and no spread operator. Combining collections means addAll or streams,
 * each collection type has its own API, and List.of's immutability is invisible in the type:
 * add() compiles and throws at runtime. (Map.of also stops at 10 pairs - then Map.ofEntries.)
 */
public final class CollectionLiterals {

    private CollectionLiterals() {}

    /** Promo first, then best sellers, then new arrivals. */
    public static List<Product> featured(Product promo, List<Product> bestSellers, List<Product> newArrivals) {
        List<Product> result = new ArrayList<>(1 + bestSellers.size() + newArrivals.size());
        result.add(promo);
        result.addAll(bestSellers);
        result.addAll(newArrivals);
        return result;
    }

    /** Arrays need yet another API. */
    public static int[] mergeQuantities(int[] first, int[] second) {
        return IntStream.concat(Arrays.stream(first), Arrays.stream(second)).toArray();
    }

    /** A mutable list needs a copy, because List.of(...) is immutable. */
    public static List<String> defaultTags() {
        return new ArrayList<>(List.of("sale", "new"));
    }

    public static final Set<String> SHIPPING_COUNTRIES = Set.of("RS", "DE", "AT");

    public static List<Order> noOrders() {
        return new ArrayList<>();
    }
}
