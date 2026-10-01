// VERDICT | T07 Events & functions | BETTER: TIE
// WHY: C#: events (+=, -=, owner-only raise) and closures that modify locals; Java: built-in composition (and/negate/andThen) and effectively-final capture.

package shop.t07_events;

import shop.domain.Category;
import shop.domain.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Where Java wins: functional interfaces ship with composition built in
 * (Predicate.and/or/negate/not, Function.andThen/compose) - C# Func/Predicate have none.
 */
public final class ProductFilters {

    private ProductFilters() {}

    public static final Predicate<Product> IS_BOOK = p -> p.category() == Category.BOOKS;
    public static final Predicate<Product> IS_CHEAP = p -> p.price().compareTo(new BigDecimal("40")) < 0;

    public static final Predicate<Product> EXPENSIVE_NON_BOOK = IS_BOOK.negate().and(IS_CHEAP.negate());
    public static final Predicate<Product> BOOK_OR_CHEAP = IS_BOOK.or(IS_CHEAP);

    public static final Function<BigDecimal, BigDecimal> ADD_VAT = p -> p.multiply(new BigDecimal("1.20"));
    public static final Function<BigDecimal, BigDecimal> ROUND = p -> p.setScale(2, RoundingMode.HALF_EVEN);
    public static final Function<BigDecimal, BigDecimal> GROSS_PRICE = ADD_VAT.andThen(ROUND);

    public static List<String> names(List<Product> products, Predicate<Product> filter) {
        return products.stream().filter(filter).map(Product::name).toList();
    }
}
