// VERDICT | T18 Declarative HTTP clients | BETTER: SPRING
// WHY: an annotated interface becomes a working client (@HttpExchange) and MockRestServiceServer tests it; .NET writes typed HttpClient code by hand (Refit is third-party) and ships no mock server.

package shop.t18_httpclients;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import shop.t18_httpclients.ReorderService.ReorderResult;
import shop.t18_httpclients.ReorderService.SupplierOutOfStockException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/** MockRestServiceServer (built into spring-test) plays the supplier: it checks each request and answers it. */
class SupplierClientTest {

    private final RestClient.Builder builder = RestClient.builder();
    private final MockRestServiceServer supplier = MockRestServiceServer.bindTo(builder).build();
    private final ReorderService reorders = new ReorderService(SupplierClientConfig.create(builder, "http://supplier.test"));

    @Test
    void reorderChecksStockThenPlacesOrder() {
        supplier.expect(requestTo("http://supplier.test/supplier/stock/B1")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"sku": "B1", "available": 50}""", MediaType.APPLICATION_JSON));
        supplier.expect(requestTo("http://supplier.test/supplier/orders")).andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.sku").value("B1"))
                .andExpect(jsonPath("$.quantity").value(20))
                .andRespond(withSuccess("""
                        {"reference": "PO-77"}""", MediaType.APPLICATION_JSON));

        assertThat(reorders.reorder("B1", 20)).isEqualTo(new ReorderResult("B1", 20, "PO-77"));
        supplier.verify();
    }

    @Test
    void noOrderWhenSupplierHasTooLittle() {
        supplier.expect(requestTo("http://supplier.test/supplier/stock/B1"))
                .andRespond(withSuccess("""
                        {"sku": "B1", "available": 5}""", MediaType.APPLICATION_JSON));

        assertThrows(SupplierOutOfStockException.class, () -> reorders.reorder("B1", 20));
        supplier.verify(); // only the stock call happened
    }

    @Test
    void httpErrorsBecomeTypedExceptions() {
        supplier.expect(requestTo("http://supplier.test/supplier/stock/X9")).andRespond(withResourceNotFound());
        assertThrows(HttpClientErrorException.NotFound.class, () -> reorders.reorder("X9", 1));
    }
}
