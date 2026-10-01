// VERDICT | T21 Output caching | BETTER: ASP.NET
// WHY: OutputCache stores whole HTTP responses (vary by query, tag eviction) without touching service code; Spring has only method-level @Cacheable - the controller and JSON serialization still run.

package shop.t21_caching;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext // changes a price
class CachingTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    CachedCatalog catalog;

    @Test
    void repeatedRequestsAreServedFromTheCache() throws Exception {
        int before = catalog.databaseQueries();
        mvc.perform(get("/api/cached/products").param("category", "GROCERY")).andExpect(status().isOk());
        mvc.perform(get("/api/cached/products").param("category", "GROCERY")).andExpect(status().isOk());
        assertThat(catalog.databaseQueries()).isEqualTo(before + 1);

        mvc.perform(get("/api/cached/products").param("category", "ELECTRONICS"));
        assertThat(catalog.databaseQueries()).isEqualTo(before + 2); // different key
    }

    @Test
    void updateEvictsTheCache() throws Exception {
        mvc.perform(get("/api/cached/products").param("category", "BOOKS"));
        int before = catalog.databaseQueries();

        mvc.perform(put("/api/cached/products/B1/price").param("price", "30.00")).andExpect(status().isNoContent());
        mvc.perform(get("/api/cached/products").param("category", "BOOKS"))
                .andExpect(jsonPath("$[0].price").value(30.0));
        assertThat(catalog.databaseQueries()).isEqualTo(before + 1);
    }
}
