// VERDICT | T02 Dependency injection | BETTER: SPRING
// WHY: component scanning, Map<name, bean> injection and scoped proxies (request bean inside a singleton); ASP.NET registers everything by hand (easier to trace).

package shop.t02_di;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DependencyInjectionTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ApplicationContext context;

    @Test
    void scannedImplementationsAreInjectedAsMapByName() throws Exception {
        mvc.perform(get("/api/payments"))
                .andExpect(jsonPath("$", contains("bank", "card", "paypal")));
        assertThat(context.getBeansOfType(PaymentProvider.class)).containsOnlyKeys("bank", "card", "paypal");
    }

    @Test
    void feeComesFromTheNamedImplementation() throws Exception {
        mvc.perform(get("/api/payments/card/fee").param("amount", "100"))
                .andExpect(jsonPath("$.fee").value(1.75));
        mvc.perform(get("/api/payments/paypal/fee").param("amount", "100"))
                .andExpect(jsonPath("$.fee").value(3.25));
        mvc.perform(get("/api/payments/crypto/fee").param("amount", "100"))
                .andExpect(status().isNotFound());
    }

    @Test
    void singletonSeesTheCurrentRequestThroughAScopedProxy() throws Exception {
        var first = requestIds();
        var second = requestIds();
        assertThat(first.get("singletonLogger")).isEqualTo(first.get("controller"));
        assertThat(second.get("controller")).isNotEqualTo(first.get("controller"));
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> requestIds() throws Exception {
        String json = mvc.perform(get("/api/di/request-ids")).andReturn().getResponse().getContentAsString();
        return com.jayway.jsonpath.JsonPath.read(json, "$");
    }
}
