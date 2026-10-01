// VERDICT | T04 Validation & ProblemDetails | BETTER: ASP.NET
// WHY: AddValidation() + AddProblemDetails() return RFC 9457 errors per field out of the box; Spring returns ProblemDetail too but needs an advice to list field errors.

package shop.t04_validation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext // creates a product
class ValidationTest {

    @Autowired
    MockMvc mvc;

    @Test
    void invalidBodyListsEveryFieldError() throws Exception {
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku": "bad", "name": "", "price": 0, "stock": -1}"""))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errors.sku[0]").value("must look like B12"))
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.category").exists())
                .andExpect(jsonPath("$.errors.price").exists())
                .andExpect(jsonPath("$.errors.stock").exists());
    }

    @Test
    void validBodyCreatesProduct() throws Exception {
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku": "Z1", "name": "Tea", "category": "GROCERY", "price": 4.20, "stock": 10}"""))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/products/Z1"))
                .andExpect(jsonPath("$.name").value("Tea"));
    }

    @Test
    void duplicateSkuIsConflict() throws Exception {
        mvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku": "B1", "name": "Copy", "category": "BOOKS", "price": 1, "stock": 1}"""))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("SKU B1 already exists"));
    }
}
