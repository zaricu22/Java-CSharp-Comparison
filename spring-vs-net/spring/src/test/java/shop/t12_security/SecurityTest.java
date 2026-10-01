// VERDICT | T12 Security | BETTER: SPRING
// WHY: Spring Security ships HTTP Basic, in-memory users and @PreAuthorize expressions over method arguments; ASP.NET needs a custom Basic handler and an authorization handler for the same rules.

package shop.t12_security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext // restocks a product
class SecurityTest {

    private static final String RESTOCK = "/api/admin/products/E1/restock";

    @Autowired
    MockMvc mvc;

    @Test
    void anonymousIsUnauthorized() throws Exception {
        mvc.perform(post(RESTOCK).param("quantity", "10")).andExpect(status().isUnauthorized());
        mvc.perform(post(RESTOCK).param("quantity", "10").with(httpBasic("admin", "wrong")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void clerkIsForbiddenByMethodSecurity() throws Exception {
        mvc.perform(post(RESTOCK).param("quantity", "10").with(httpBasic("clerk", "clerk-pass")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanRestockWithinTheLimit() throws Exception {
        mvc.perform(post(RESTOCK).param("quantity", "10").with(httpBasic("admin", "admin-pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(13));
    }

    @Test
    void expressionAlsoChecksTheArgument() throws Exception {
        mvc.perform(post(RESTOCK).param("quantity", "5000").with(httpBasic("admin", "admin-pass")))
                .andExpect(status().isForbidden());
    }
}
