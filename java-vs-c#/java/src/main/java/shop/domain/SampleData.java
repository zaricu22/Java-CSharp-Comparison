// VERDICT | Domain model | BETTER: C#
// WHY: positional records with computed members fit in one file; Java needs one file per public type and BigDecimal methods instead of decimal operators.

package shop.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** The same data set exists on the C# side, so both test suites assert identical numbers. */
public final class SampleData {

    private SampleData() {}

    public static final Product CLEAN_CODE  = new Product("B1", "Clean Code",  Category.BOOKS,       new BigDecimal("35.50"));
    public static final Product REFACTORING = new Product("B2", "Refactoring", Category.BOOKS,       new BigDecimal("42.00"));
    public static final Product KEYBOARD    = new Product("E1", "Keyboard",    Category.ELECTRONICS, new BigDecimal("89.99"));
    public static final Product MONITOR     = new Product("E2", "Monitor",     Category.ELECTRONICS, new BigDecimal("249.00"));
    public static final Product COFFEE      = new Product("G1", "Coffee",      Category.GROCERY,     new BigDecimal("12.40"));

    public static final Customer ANA    = new Customer(1, "Ana",    "Belgrade");
    public static final Customer MARKO  = new Customer(2, "Marko",  "Novi Sad");
    public static final Customer JELENA = new Customer(3, "Jelena", "Belgrade");

    public static List<Product> products() {
        return List.of(CLEAN_CODE, REFACTORING, KEYBOARD, MONITOR, COFFEE);
    }

    public static List<Order> orders() {
        return List.of(
                new Order(1, ANA,    LocalDate.of(2026, 1, 10), List.of(new OrderLine(CLEAN_CODE, 2), new OrderLine(KEYBOARD, 1))),
                new Order(2, MARKO,  LocalDate.of(2026, 1, 15), List.of(new OrderLine(MONITOR, 1), new OrderLine(COFFEE, 3))),
                new Order(3, ANA,    LocalDate.of(2026, 2, 2),  List.of(new OrderLine(COFFEE, 5))),
                new Order(4, JELENA, LocalDate.of(2026, 2, 20), List.of(new OrderLine(REFACTORING, 1), new OrderLine(KEYBOARD, 2))));
    }
}
