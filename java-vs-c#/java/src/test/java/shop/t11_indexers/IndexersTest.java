// VERDICT | T11 Indexers & ranges | BETTER: C#
// WHY: catalog["E1"], matrix[1, 1], items[1..^1]; Java spells everything as get()/subList()/substring().

package shop.t11_indexers;

import org.junit.jupiter.api.Test;
import shop.domain.Category;
import shop.domain.Product;
import shop.domain.SampleData;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IndexersTest {

    @Test
    void lookupBySkuAndCategory() {
        var catalog = new Catalog(SampleData.products());
        assertEquals("Keyboard", catalog.get("E1").name());
        assertEquals(List.of("Clean Code", "Refactoring"),
                catalog.get(Category.BOOKS).stream().map(Product::name).toList());
        assertThrows(NoSuchElementException.class, () -> catalog.get("X9"));
    }

    @Test
    void replaceBySku() {
        var catalog = new Catalog(SampleData.products());
        catalog.put(new Product("E1", "Mechanical Keyboard", Category.ELECTRONICS, new BigDecimal("129.00")));
        assertEquals("Mechanical Keyboard", catalog.get("E1").name());
    }

    @Test
    void twoDimensionalLookup() {
        var matrix = new ShippingMatrix();
        assertEquals(new BigDecimal("14.90"), matrix.get(1, 1));
        matrix.set(1, 1, new BigDecimal("13.90"));
        assertEquals(new BigDecimal("13.90"), matrix.get(1, 1));
    }

    @Test
    void rangesFromTheEnd() {
        var skus = List.of("B1", "B2", "E1", "E2", "G1");
        assertEquals(List.of("E2", "G1"), Ranges.lastN(skus, 2));
        assertEquals(List.of("B2", "E1", "E2"), Ranges.withoutFirstAndLast(skus));
        assertEquals("B", Ranges.skuPrefix("B12"));
        assertEquals("12", Ranges.lastChars("B12", 2));
    }
}
