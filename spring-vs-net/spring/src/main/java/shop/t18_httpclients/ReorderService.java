// VERDICT | T18 Declarative HTTP clients | BETTER: SPRING
// WHY: an annotated interface becomes a working client (@HttpExchange) and MockRestServiceServer tests it; .NET writes typed HttpClient code by hand (Refit is third-party) and ships no mock server.

package shop.t18_httpclients;

import org.springframework.stereotype.Service;
import shop.t18_httpclients.SupplierApi.SupplierOrderRequest;

/** Business code only sees the interface - no HTTP details. */
@Service
public class ReorderService {

    public record ReorderResult(String sku, int quantity, String reference) {}

    public static class SupplierOutOfStockException extends RuntimeException {
        public SupplierOutOfStockException(String sku, int available) {
            super("Supplier has only " + available + " of " + sku);
        }
    }

    private final SupplierApi supplier;

    public ReorderService(SupplierApi supplier) {
        this.supplier = supplier;
    }

    public ReorderResult reorder(String sku, int quantity) {
        var stock = supplier.stock(sku);
        if (stock.available() < quantity) {
            throw new SupplierOutOfStockException(sku, stock.available());
        }
        var order = supplier.order(new SupplierOrderRequest(sku, quantity));
        return new ReorderResult(sku, quantity, order.reference());
    }
}
