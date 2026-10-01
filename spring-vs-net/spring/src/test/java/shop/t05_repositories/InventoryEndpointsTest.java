// VERDICT | T05 Repositories | BETTER: SPRING
// WHY: Spring Data derives queries from method names with zero implementation; EF's DbSet is already a repository, but every query is written as LINQ.

package shop.t05_repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryEndpointsTest {

    @Autowired
    MockMvc mvc;

    @Test
    void affordableInStockProducts() throws Exception {
        mvc.perform(get("/api/inventory/affordable").param("maxPrice", "50"))
                .andExpect(jsonPath("$[*].sku", contains("G1", "B1", "B2")));
    }

    @Test
    void lowStockCount() throws Exception {
        mvc.perform(get("/api/inventory/low-stock-count").param("threshold", "5"))
                .andExpect(jsonPath("$.count").value(2));
    }
}
