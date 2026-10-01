// VERDICT | T08 Async | BETTER: TIE
// WHY: C# async/await beats CompletableFuture; Java 21+ virtual threads allow plain blocking code with no async 'coloring'.

package shop.t08_async;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.NoSuchElementException;

/** Fake "remote" services: every call blocks for {@link #LATENCY} like a slow HTTP request. */
public final class RemoteApis {

    public static final Duration LATENCY = Duration.ofMillis(100);

    private static final Map<String, BigDecimal> PRICES = Map.of(
            "B1", new BigDecimal("35.50"), "E1", new BigDecimal("89.99"), "G1", new BigDecimal("12.40"));
    private static final Map<String, Integer> STOCK = Map.of("B1", 12, "E1", 3, "G1", 40);

    private RemoteApis() {}

    public static BigDecimal price(String sku) {
        simulateLatency();
        BigDecimal price = PRICES.get(sku);
        if (price == null) {
            throw new NoSuchElementException("Unknown sku " + sku);
        }
        return price;
    }

    public static int stock(String sku) {
        simulateLatency();
        return STOCK.getOrDefault(sku, 0);
    }

    private static void simulateLatency() {
        try {
            Thread.sleep(LATENCY);
        } catch (InterruptedException e) { // checked exception - cannot just let it fly inside a lambda
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
