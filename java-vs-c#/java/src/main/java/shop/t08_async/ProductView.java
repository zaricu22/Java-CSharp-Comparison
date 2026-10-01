// VERDICT | T08 Async | BETTER: TIE
// WHY: C# async/await beats CompletableFuture; Java 21+ virtual threads allow plain blocking code with no async 'coloring'.

package shop.t08_async;

import java.math.BigDecimal;

public record ProductView(String sku, BigDecimal price, int stock) {}
