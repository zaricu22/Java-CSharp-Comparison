// VERDICT | T03 Type system & generics | BETTER: C#
// WHY: reified generics (typeof(T), new T(), List<int> without boxing) and generic math; Java has erasure and Integer boxing traps.

package shop.t03_types;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TypeSystemTest {

    @Test
    void primitivesNeedWrapperStatics() {
        assertEquals("255 units (0xff)", TypeSystem.describeStock(255));
    }

    @Test
    void boxedNumbersCompareByReference() {
        Integer a = 1000;
        Integer b = 1000;
        assertFalse(a == b);     // two different Integer objects!
        assertTrue(a.equals(b)); // value comparison needs equals()
        Integer small1 = 100;
        Integer small2 = 100;
        assertTrue(small1 == small2); // ...but -128..127 are cached, so == "works" here - a classic trap
    }

    @Test
    @SuppressWarnings({"StringEquality", "StringOperationCanBeSimplified"})
    void stringEqualityNeedsEquals() {
        String sku = new String("B1"); // e.g. a value read from a request or a file
        assertFalse(sku == "B1");      // == compares references for every object, String included
        assertTrue(sku.equals("B1"));
    }

    @Test
    void boxedListCanHideNulls() {
        List<Integer> quantities = Arrays.asList(2, null, 3);
        assertThrows(NullPointerException.class, () -> TypeSystem.totalUnits(quantities));
    }

    @Test
    void genericSumNeedsZeroAndPlusPassedIn() {
        assertEquals(6, TypeSystem.sum(List.of(1, 2, 3), 0, Integer::sum));
        assertEquals(new BigDecimal("3.30"),
                TypeSystem.sum(List.of(new BigDecimal("1.10"), new BigDecimal("2.20")), BigDecimal.ZERO, BigDecimal::add));
    }

    @Test
    void repositoryNeedsClassTokenAndFactory() {
        var repo = new Repository<>(CartDraft.class, CartDraft::new);
        var draft = repo.createNew();
        assertEquals("CartDraft", repo.entityName());
        assertEquals("new cart", draft.getNote());
        assertEquals(1, repo.count());
    }

    @Test
    void erasureCannotTellEmptyListTypes() {
        assertTrue(TypeSystem.isListOfStrings(List.of("a", "b")));
        assertFalse(TypeSystem.isListOfStrings(List.of(1, 2)));
        List<Integer> emptyInts = List.of();
        assertTrue(TypeSystem.isListOfStrings(emptyInts)); // wrong answer, but the best erasure allows
    }
}
