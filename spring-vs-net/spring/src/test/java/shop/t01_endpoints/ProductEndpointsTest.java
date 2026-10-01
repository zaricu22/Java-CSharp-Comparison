// VERDICT | T01 Endpoints & hosting | BETTER: ASP.NET
// WHY: minimal APIs map a route to a lambda with typed results in one line; Spring needs a @RestController class (Java 21 sealed switch maps results as cleanly as C#).

package shop.t01_endpoints;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductEndpointsTest {

    @Autowired
    MockMvc mvc;

    @Test
    void listsAllProducts() throws Exception {
        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].sku").value("B1"))
                .andExpect(jsonPath("$[0].price").value(35.5));
    }

    @Test
    void filtersByCategory() throws Exception {
        mvc.perform(get("/api/products").param("category", "BOOKS"))
                .andExpect(jsonPath("$[*].sku", contains("B1", "B2")));
    }

    @Test
    void getsOneProduct() throws Exception {
        mvc.perform(get("/api/products/E1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.stock").value(3));
    }

    @Test
    void unknownProductIsProblemDetails404() throws Exception {
        mvc.perform(get("/api/products/X9"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Product X9 not found"));
    }
}
