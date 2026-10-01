// VERDICT | T18 Declarative HTTP clients | BETTER: SPRING
// WHY: an annotated interface becomes a working client (@HttpExchange) and MockRestServiceServer tests it; .NET writes typed HttpClient code by hand (Refit is third-party) and ships no mock server.

package shop.t18_httpclients;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * The whole client for the supplier's REST API: an interface. Spring generates the implementation
 * (URL building, JSON in/out, 4xx/5xx -> exceptions) - the same annotations style as the controllers.
 */
@HttpExchange("/supplier")
public interface SupplierApi {

    record SupplierStock(String sku, int available) {}

    record SupplierOrderRequest(String sku, int quantity) {}

    record SupplierOrder(String reference) {}

    @GetExchange("/stock/{sku}")
    SupplierStock stock(@PathVariable String sku);

    @PostExchange("/orders")
    SupplierOrder order(@RequestBody SupplierOrderRequest request);
}
