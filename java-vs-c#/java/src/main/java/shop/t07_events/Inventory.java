// VERDICT | T07 Events & functions | BETTER: TIE
// WHY: C#: events (+=, -=, owner-only raise) and closures that modify locals; Java: built-in composition (and/negate/andThen) and effectively-final capture.

package shop.t07_events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * No events in the language: the observer pattern is written by hand -
 * a listener list, subscribe/unsubscribe methods and a loop to fire.
 */
public final class Inventory {

    public record StockLow(String sku, int remaining) {}

    private final Map<String, Integer> stock = new HashMap<>();
    private final List<Consumer<StockLow>> stockLowListeners = new CopyOnWriteArrayList<>();
    private final int threshold;

    public Inventory(int threshold) {
        this.threshold = threshold;
    }

    public void addStockLowListener(Consumer<StockLow> listener) {
        stockLowListeners.add(listener);
    }

    public void removeStockLowListener(Consumer<StockLow> listener) {
        stockLowListeners.remove(listener);
    }

    public void add(String sku, int quantity) {
        stock.merge(sku, quantity, Integer::sum);
    }

    public void remove(String sku, int quantity) {
        int left = stock.merge(sku, -quantity, Integer::sum);
        if (left < threshold) {
            var event = new StockLow(sku, left);
            stockLowListeners.forEach(l -> l.accept(event));
        }
    }

    public int stockOf(String sku) {
        return stock.getOrDefault(sku, 0);
    }
}
