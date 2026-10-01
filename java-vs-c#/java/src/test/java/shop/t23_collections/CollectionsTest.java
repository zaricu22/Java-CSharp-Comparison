// VERDICT | T23 Collection expressions | BETTER: C#
// WHY: one [a, ..b, ..c] syntax builds arrays, lists, sets and immutable collections; Java mixes List.of, addAll and streams, and List.of hides immutability until add() throws.

package shop.t23_collections;

import org.junit.jupiter.api.Test;
import shop.domain.Product;
import shop.domain.SampleData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectionsTest {

    @Test
    void combineElementAndSequences() {
        var featured = CollectionLiterals.featured(SampleData.MONITOR,
                List.of(SampleData.COFFEE, SampleData.CLEAN_CODE), List.of(SampleData.REFACTORING));
        assertEquals(List.of("Monitor", "Coffee", "Clean Code", "Refactoring"), featured.stream().map(Product::name).toList());
    }

    @Test
    void mergeArrays() {
        assertArrayEquals(new int[] {2, 1, 3, 5}, CollectionLiterals.mergeQuantities(new int[] {2, 1}, new int[] {3, 5}));
    }

    @Test
    void mutableListCanGrow() {
        var tags = CollectionLiterals.defaultTags();
        tags.add("bestseller");
        assertEquals(List.of("sale", "new", "bestseller"), tags);
    }

    @Test
    void immutabilityIsOnlyVisibleAtRuntime() {
        List<String> fixed = List.of("sale", "new");
        assertThrows(UnsupportedOperationException.class, () -> fixed.add("x")); // add() exists on List, but throws
        assertTrue(CollectionLiterals.SHIPPING_COUNTRIES.contains("DE"));
        assertThrows(UnsupportedOperationException.class, () -> CollectionLiterals.SHIPPING_COUNTRIES.add("US"));
    }

    @Test
    void emptyCollection() {
        assertTrue(CollectionLiterals.noOrders().isEmpty());
    }
}
