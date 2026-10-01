// VERDICT | T17 Generic variance | BETTER: JAVA
// WHY: use-site `? extends` / `? super` works on any type; C# has no lower bound, so consumers need a delegate workaround.

package shop.t17_variance;

import org.junit.jupiter.api.Test;
import shop.t17_variance.Sellable.Book;
import shop.t17_variance.Sellable.Ebook;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class VarianceTest {

    private final List<Book> books = List.of(
            new Book("Clean Code", new BigDecimal("35.50"), 464),
            new Book("Refactoring", new BigDecimal("42.00"), 448));

    @Test
    void producerAcceptsListOfSubtype() {
        assertEquals(new BigDecimal("77.50"), Variance.total(books));
    }

    @Test
    void consumerAcceptsListOfSupertype() {
        List<Object> anything = new ArrayList<>();
        List<Sellable> sellables = new ArrayList<>();
        List<Ebook> ebooks = new ArrayList<>();

        Variance.addFreebie(anything);
        Variance.addFreebie(sellables);
        Variance.addFreebie(ebooks);

        assertInstanceOf(Ebook.class, anything.getFirst());
        assertEquals("Java Cheat Sheet", sellables.getFirst().title());
        assertEquals(1, ebooks.size());
    }

    @Test
    void copyFromSubtypeListIntoSupertypeList() {
        List<Sellable> basket = new ArrayList<>();
        Variance.copyMatching(books, basket, b -> b.price().compareTo(new BigDecimal("40")) < 0);
        assertEquals(List.of("Clean Code"), basket.stream().map(Sellable::title).toList());
    }

    @Test
    void comparatorOfSupertypeWorksForSubtype() {
        Comparator<Sellable> byPrice = Comparator.comparing(Sellable::price);
        List<Ebook> ebooks = List.of(
                new Ebook("Kotlin in Action", new BigDecimal("30"), 12),
                new Ebook("Effective Java", new BigDecimal("28"), 9));
        assertEquals("Effective Java", Variance.cheapest(ebooks, byPrice).title());
    }
}
