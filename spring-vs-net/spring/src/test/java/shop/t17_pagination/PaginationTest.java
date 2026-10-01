// VERDICT | T17 Pagination & sorting | BETTER: SPRING
// WHY: a Pageable parameter gives page/size/sort binding, defaults, a max page size and a Page with totals from the repository; ASP.NET writes Skip/Take, the count query, sort parsing and the page DTO by hand.

package shop.t17_pagination;

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
class PaginationTest {

    @Autowired
    MockMvc mvc;

    @Test
    void firstPageSortedByPriceDescending() throws Exception {
        mvc.perform(get("/api/products/paged").param("page", "0").param("size", "2").param("sort", "price,desc"))
                .andExpect(jsonPath("$.content[*].sku", contains("E2", "E1")))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.size").value(2))
                .andExpect(jsonPath("$.page.totalElements").value(5))
                .andExpect(jsonPath("$.page.totalPages").value(3));
    }

    @Test
    void lastPageHasTheRemainder() throws Exception {
        mvc.perform(get("/api/products/paged").param("page", "2").param("size", "2").param("sort", "price,desc"))
                .andExpect(jsonPath("$.content[*].sku", contains("G1")));
    }

    @Test
    void derivedQueryIsPagedToo() throws Exception {
        mvc.perform(get("/api/products/paged").param("category", "BOOKS").param("sort", "price,desc"))
                .andExpect(jsonPath("$.content[*].sku", contains("B2", "B1")))
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    void defaultsAndMaximumSizeApply() throws Exception {
        mvc.perform(get("/api/products/paged"))
                .andExpect(jsonPath("$.content[0].sku").value("B1")) // default sort = sku
                .andExpect(jsonPath("$.page.size").value(20));      // default size
        mvc.perform(get("/api/products/paged").param("size", "100000"))
                .andExpect(jsonPath("$.page.size").value(2000));    // capped, protects the database
    }
}
