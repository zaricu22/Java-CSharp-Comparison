// VERDICT | T08 Async | BETTER: TIE
// WHY: C# async/await beats CompletableFuture; Java 21+ virtual threads allow plain blocking code with no async 'coloring'.

package shop.t08_async;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Style 2 - virtual threads (Java 21): Java's real answer to async/await.
 * Plain blocking code, plain try/catch, and no "async coloring" of method signatures -
 * blocking a virtual thread is cheap, so millions can wait on I/O at once.
 * (StructuredTaskScope would make this even tidier but is still a preview API in Java 25.)
 */
public final class CatalogVirtualThreads {

    private CatalogVirtualThreads() {}

    public static ProductView view(String sku) throws InterruptedException, ExecutionException {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<BigDecimal> price = executor.submit(() -> RemoteApis.price(sku));
            Future<Integer> stock = executor.submit(() -> RemoteApis.stock(sku));
            return new ProductView(sku, price.get(), stock.get());
        }
    }

    public static List<ProductView> views(List<String> skus) throws InterruptedException, ExecutionException {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<ProductView>> futures = new ArrayList<>();
            for (String sku : skus) {
                futures.add(executor.submit(() -> view(sku)));
            }
            List<ProductView> result = new ArrayList<>();
            for (Future<ProductView> f : futures) {
                result.add(f.get());
            }
            return result;
        }
    }

    public static BigDecimal priceOrDefault(String sku, BigDecimal fallback) {
        try {
            return RemoteApis.price(sku);
        } catch (NoSuchElementException _) {
            return fallback;
        }
    }
}
