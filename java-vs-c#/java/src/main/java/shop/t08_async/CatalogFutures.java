// VERDICT | T08 Async | BETTER: TIE
// WHY: C# async/await beats CompletableFuture; Java 21+ virtual threads allow plain blocking code with no async 'coloring'.

package shop.t08_async;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Style 1 - CompletableFuture: the classic Java "async" API.
 * Non-blocking, but the logic is spread across callbacks (thenCombine, allOf, exceptionally)
 * and exceptions arrive wrapped in CompletionException.
 */
public final class CatalogFutures {

    private static final ExecutorService IO = Executors.newVirtualThreadPerTaskExecutor();

    private CatalogFutures() {}

    public static CompletableFuture<BigDecimal> priceAsync(String sku) {
        return CompletableFuture.supplyAsync(() -> RemoteApis.price(sku), IO);
    }

    public static CompletableFuture<Integer> stockAsync(String sku) {
        return CompletableFuture.supplyAsync(() -> RemoteApis.stock(sku), IO);
    }

    public static CompletableFuture<ProductView> viewAsync(String sku) {
        return priceAsync(sku).thenCombine(stockAsync(sku), (price, stock) -> new ProductView(sku, price, stock));
    }

    public static CompletableFuture<List<ProductView>> viewsAsync(List<String> skus) {
        List<CompletableFuture<ProductView>> futures = skus.stream().map(CatalogFutures::viewAsync).toList();
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(ignored -> futures.stream().map(CompletableFuture::join).toList());
    }

    public static CompletableFuture<BigDecimal> priceOrDefaultAsync(String sku, BigDecimal fallback) {
        return priceAsync(sku).exceptionally(ex -> {
            Throwable cause = ex instanceof CompletionException ? ex.getCause() : ex;
            if (cause instanceof NoSuchElementException) {
                return fallback;
            }
            throw new CompletionException(cause);
        });
    }
}
