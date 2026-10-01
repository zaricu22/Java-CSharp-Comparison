// VERDICT | T08 Async | BETTER: TIE
// WHY: C# async/await beats CompletableFuture; Java 21+ virtual threads allow plain blocking code with no async 'coloring'.

package shop.t08_async;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsyncTest {

    private static final List<String> SKUS = List.of("B1", "E1", "G1");
    private static final ProductView KEYBOARD = new ProductView("E1", new BigDecimal("89.99"), 3);

    /** 3 products x 2 calls x 100 ms = 600 ms sequentially; concurrently it is ~100 ms. */
    private static final long CONCURRENT_LIMIT_MS = 400;

    @Test
    void futuresCombinePriceAndStock() {
        assertEquals(KEYBOARD, CatalogFutures.viewAsync("E1").join());
    }

    @Test
    void futuresRunConcurrently() {
        long start = System.nanoTime();
        var views = CatalogFutures.viewsAsync(SKUS).join();
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        assertEquals(3, views.size());
        assertEquals(KEYBOARD, views.get(1));
        assertTrue(elapsedMs < CONCURRENT_LIMIT_MS, "took " + elapsedMs + " ms");
    }

    @Test
    void futuresErrorsArriveWrapped() {
        var ex = assertThrows(CompletionException.class, () -> CatalogFutures.priceAsync("X9").join());
        assertInstanceOf(NoSuchElementException.class, ex.getCause());
        assertEquals(BigDecimal.ZERO, CatalogFutures.priceOrDefaultAsync("X9", BigDecimal.ZERO).join());
    }

    @Test
    void virtualThreadsRunConcurrently() throws Exception {
        long start = System.nanoTime();
        var views = CatalogVirtualThreads.views(SKUS);
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        assertEquals(KEYBOARD, views.get(1));
        assertTrue(elapsedMs < CONCURRENT_LIMIT_MS, "took " + elapsedMs + " ms");
    }

    @Test
    void virtualThreadsUsePlainTryCatch() {
        assertEquals(BigDecimal.ZERO, CatalogVirtualThreads.priceOrDefault("X9", BigDecimal.ZERO));
        assertEquals(new BigDecimal("35.50"), CatalogVirtualThreads.priceOrDefault("B1", BigDecimal.ZERO));
    }
}
