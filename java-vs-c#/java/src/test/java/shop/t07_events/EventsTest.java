// VERDICT | T07 Events & functions | BETTER: TIE
// WHY: C#: events (+=, -=, owner-only raise) and closures that modify locals; Java: built-in composition (and/negate/andThen) and effectively-final capture.

package shop.t07_events;

import org.junit.jupiter.api.Test;
import shop.domain.SampleData;
import shop.t07_events.Inventory.StockLow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventsTest {

    @Test
    void listenerIsNotifiedWhenStockDropsBelowThreshold() {
        var inventory = new Inventory(5);
        List<StockLow> received = new ArrayList<>();
        inventory.addStockLowListener(received::add);

        inventory.add("G1", 10);
        inventory.remove("G1", 3); // 7 left - no event
        inventory.remove("G1", 4); // 3 left - event

        assertEquals(List.of(new StockLow("G1", 3)), received);
        assertEquals(3, inventory.stockOf("G1"));
    }

    @Test
    void removedListenerIsNotNotified() {
        var inventory = new Inventory(5);
        List<StockLow> received = new ArrayList<>();
        Consumer<StockLow> listener = received::add; // must keep the same reference to unsubscribe
        inventory.addStockLowListener(listener);
        inventory.removeStockLowListener(listener);

        inventory.add("G1", 1);
        inventory.remove("G1", 1);

        assertTrue(received.isEmpty());
    }

    @Test
    void lambdasCaptureOnlyEffectivelyFinalLocals() {
        var inventory = new Inventory(5);
        // int count = 0; inventory.addStockLowListener(e -> count++);
        //   -> compile error: local variables referenced from a lambda must be final or effectively final
        var count = new AtomicInteger();
        inventory.addStockLowListener(e -> count.incrementAndGet());
        inventory.add("G1", 2);
        inventory.remove("G1", 1);
        inventory.remove("G1", 1);
        assertEquals(2, count.get());
    }

    @Test
    void loopVariablesCannotBeCapturedByMistake() {
        List<Supplier<Integer>> actions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int copy = i; // "() -> i" would not compile, which prevents the classic shared-variable bug
            actions.add(() -> copy);
        }
        assertEquals(List.of(0, 1, 2), actions.stream().map(Supplier::get).toList());
    }

    @Test
    void predicatesComposeWithBuiltInMethods() {
        var products = SampleData.products();
        assertEquals(List.of("Keyboard", "Monitor"), ProductFilters.names(products, ProductFilters.EXPENSIVE_NON_BOOK));
        assertEquals(List.of("Clean Code", "Refactoring", "Coffee"), ProductFilters.names(products, ProductFilters.BOOK_OR_CHEAP));
    }

    @Test
    void functionsComposeWithAndThen() {
        assertEquals(new BigDecimal("107.99"), ProductFilters.GROSS_PRICE.apply(new BigDecimal("89.99")));
    }
}
