// VERDICT | T09 AOP / cross-cutting | BETTER: SPRING
// WHY: an @Aspect intercepts any bean method, including the service layer; ASP.NET endpoint filters and middleware only wrap HTTP requests.

package shop.t09_aop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import shop.t07_transactions.OrderRequest;
import shop.t07_transactions.OrderService;
import shop.t07_transactions.OrderService.UnknownProductException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext // creates a product
class AopTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    OrderService orders;

    @Autowired
    AuditAspect audit;

    @Test
    void aspectWrapsServiceCallsFromHttp() throws Exception {
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"sku": "Z2", "name": "Mug", "category": "GROCERY", "price": 6.50, "stock": 4}"""));
        assertThat(audit.trail()).contains("CatalogService.register ok");
    }

    @Test
    void aspectAlsoWrapsDirectServiceCalls() {
        // No HTTP involved: a scheduled job or another service calling this is audited too
        assertThrows(UnknownProductException.class,
                () -> orders.placeOrder(new OrderRequest("Eva", List.of(new OrderRequest.Line("X9", 1)))));
        assertThat(audit.trail()).contains("OrderService.placeOrder failed: UnknownProductException");
    }
}
