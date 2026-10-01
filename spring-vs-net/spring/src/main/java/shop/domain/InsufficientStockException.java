// VERDICT | Domain model | BETTER: ASP.NET
// WHY: EF Core maps plain classes by convention (properties, no annotations); JPA needs @Entity/@Id, a protected no-arg constructor and getters.

package shop.domain;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String sku, int available, int requested) {
        super("Only " + available + " of " + sku + " in stock, " + requested + " requested");
    }
}
