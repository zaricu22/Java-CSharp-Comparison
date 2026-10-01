// VERDICT | T23 Collection expressions | BETTER: C#
// WHY: one [a, ..b, ..c] syntax builds arrays, lists, sets and immutable collections; Java mixes List.of, addAll and streams, and List.of hides immutability until add() throws.

package shop.t23_collections;

import shop.domain.Product;
import shop.domain.SampleData;

import java.util.Arrays;
import java.util.List;

public final class Demo {

    public static void run() {
        var featured = CollectionLiterals.featured(SampleData.MONITOR,
                List.of(SampleData.COFFEE, SampleData.CLEAN_CODE), List.of(SampleData.REFACTORING));
        System.out.println("Featured: " + featured.stream().map(Product::name).toList());
        System.out.println("Merged quantities: "
                + Arrays.toString(CollectionLiterals.mergeQuantities(new int[] {2, 1}, new int[] {3, 5})));

        var tags = CollectionLiterals.defaultTags();
        tags.add("bestseller");
        System.out.println("Tags: " + tags);
        System.out.println("Ships to DE? " + CollectionLiterals.SHIPPING_COUNTRIES.contains("DE"));
    }
}
