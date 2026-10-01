// VERDICT | T07 Events & functions | BETTER: TIE
// WHY: C#: events (+=, -=, owner-only raise) and closures that modify locals; Java: built-in composition (and/negate/andThen) and effectively-final capture.

package shop.t07_events;

import shop.domain.SampleData;

import java.math.BigDecimal;

public final class Demo {

    public static void run() {
        var inventory = new Inventory(5);
        inventory.addStockLowListener(e -> System.out.println("Reorder " + e.sku() + ", only " + e.remaining() + " left"));
        inventory.add("G1", 10);
        inventory.remove("G1", 3);
        inventory.remove("G1", 4);

        var products = SampleData.products();
        System.out.println("Expensive non-books: " + ProductFilters.names(products, ProductFilters.EXPENSIVE_NON_BOOK));
        System.out.println("Gross 89.99: " + ProductFilters.GROSS_PRICE.apply(new BigDecimal("89.99")));
    }
}
