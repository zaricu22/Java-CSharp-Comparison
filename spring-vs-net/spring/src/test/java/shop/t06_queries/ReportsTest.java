// VERDICT | T06 Complex queries | BETTER: ASP.NET
// WHY: LINQ is type-checked and refactor-safe (GroupBy/Sum/conditional Where); Spring uses JPQL strings or the verbose Criteria/Specification API with string attribute names.

package shop.t06_queries;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class ReportsTest {

    @Autowired
    MockMvc mvc;

    @Test
    void revenueByCategoryBiggestFirst() throws Exception {
        mvc.perform(get("/api/reports/revenue-by-category"))
                .andExpect(jsonPath("$[*].category", contains("ELECTRONICS", "BOOKS", "GROCERY")))
                .andExpect(jsonPath("$[0].revenue").value(518.97))
                .andExpect(jsonPath("$[1].revenue").value(113.0))
                .andExpect(jsonPath("$[2].revenue").value(99.2));
    }

    @Test
    void topCustomersSinceDate() throws Exception {
        mvc.perform(get("/api/reports/top-customers").param("since", "2026-01-01").param("limit", "2"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].customer", contains("Marko", "Ana")))
                .andExpect(jsonPath("$[0].total").value(286.2))
                .andExpect(jsonPath("$[1].total").value(222.99));
    }

    @Test
    void dynamicFilters() throws Exception {
        mvc.perform(get("/api/reports/orders")).andExpect(jsonPath("$", contains(1, 2, 3, 4)));
        mvc.perform(get("/api/reports/orders").param("category", "BOOKS")).andExpect(jsonPath("$", contains(1, 4)));
        mvc.perform(get("/api/reports/orders").param("customer", "Ana").param("category", "GROCERY"))
                .andExpect(jsonPath("$", contains(3)));
    }
}
