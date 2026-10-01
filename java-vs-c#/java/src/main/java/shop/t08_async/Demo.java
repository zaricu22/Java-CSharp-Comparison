// VERDICT | T08 Async | BETTER: TIE
// WHY: C# async/await beats CompletableFuture; Java 21+ virtual threads allow plain blocking code with no async 'coloring'.

package shop.t08_async;

import java.math.BigDecimal;
import java.util.List;

public final class Demo {

    public static void run() throws Exception {
        long start = System.nanoTime();
        List<ProductView> views = CatalogFutures.viewsAsync(List.of("B1", "E1", "G1")).join();
        System.out.printf("CompletableFuture: %s in %d ms%n", views, (System.nanoTime() - start) / 1_000_000);

        start = System.nanoTime();
        views = CatalogVirtualThreads.views(List.of("B1", "E1", "G1"));
        System.out.printf("Virtual threads:   %s in %d ms%n", views, (System.nanoTime() - start) / 1_000_000);

        System.out.println("Unknown sku price: " + CatalogVirtualThreads.priceOrDefault("X9", BigDecimal.ZERO));
    }
}
