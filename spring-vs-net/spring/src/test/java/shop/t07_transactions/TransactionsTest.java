// VERDICT | T07 Transactions | BETTER: SPRING
// WHY: @Transactional makes a whole service method atomic across repositories and nested calls; EF's SaveChanges is one unit of work, several saves need BeginTransaction/Commit.

package shop.t07_transactions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext // commits orders and stock changes
class TransactionsTest {

    @Autowired
    MockMvc mvc;

    @Test
    void successfulOrderReducesStock() throws Exception {
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customer": "Eva", "lines": [{"sku": "B1", "quantity": 2}]}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(71.0));
        mvc.perform(get("/api/products/B1")).andExpect(jsonPath("$.stock").value(10));
    }

    @Test
    void failureOnSecondLineRollsBackTheFirst() throws Exception {
        // B2 has 7 in stock, E2 has 0: the B2 change is made first, then E2 fails
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customer": "Eva", "lines": [{"sku": "B2", "quantity": 1}, {"sku": "E2", "quantity": 1}]}"""))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Only 0 of E2 in stock, 1 requested"));
        mvc.perform(get("/api/products/B2")).andExpect(jsonPath("$.stock").value(7));
    }

    @Test
    void unknownProductIs404() throws Exception {
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"customer": "Eva", "lines": [{"sku": "X9", "quantity": 1}]}"""))
                .andExpect(status().isNotFound());
    }
}
