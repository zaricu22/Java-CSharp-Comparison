// VERDICT | T17 Generic variance | BETTER: JAVA
// WHY: use-site `? extends` / `? super` works on any type; C# has no lower bound, so consumers need a delegate workaround.

package shop.t17_variance;

import shop.t17_variance.Sellable.Book;
import shop.t17_variance.Sellable.Ebook;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Demo {

    public static void run() {
        List<Book> books = List.of(new Book("Clean Code", new BigDecimal("35.50"), 464),
                new Book("Refactoring", new BigDecimal("42.00"), 448));
        System.out.println("Books total: " + Variance.total(books));

        List<Sellable> basket = new ArrayList<>();
        Variance.copyMatching(books, basket, b -> b.price().compareTo(new BigDecimal("40")) < 0);
        Variance.addFreebie(basket);
        System.out.println("Basket: " + basket.stream().map(Sellable::title).toList());

        Comparator<Sellable> byPrice = Comparator.comparing(Sellable::price);
        List<Ebook> ebooks = List.of(new Ebook("Kotlin in Action", new BigDecimal("30"), 12), new Ebook("Effective Java", new BigDecimal("28"), 9));
        System.out.println("Cheapest ebook: " + Variance.cheapest(ebooks, byPrice).title());
    }
}
