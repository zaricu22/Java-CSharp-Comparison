// VERDICT | T15 Testing | BETTER: TIE
// WHY: Spring has test slices (@WebMvcTest, @DataJpaTest) and Mockito mocks concrete classes; ASP.NET's WebApplicationFactory runs the whole app in memory, fast and simple, with interface-based fakes.

package shop.t15_testing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.domain.Category;
import shop.t01_endpoints.CatalogService;
import shop.t01_endpoints.ProductController;
import shop.t01_endpoints.ProductDto;
import shop.t12_security.SecurityConfig;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A "slice": only the web layer for ONE controller starts - no database, no services, no jobs.
 * CatalogService is a concrete class, and Mockito replaces it without needing an interface.
 */
@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
class ProductControllerSliceTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    CatalogService catalog;

    @Test
    void controllerUsesTheMockedService() throws Exception {
        when(catalog.find("Q1")).thenReturn(Optional.of(
                new ProductDto("Q1", "Mocked", Category.BOOKS, new BigDecimal("9.99"), 1)));

        mvc.perform(get("/api/products/Q1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mocked"));
    }

    @Test
    void missingProductFromMockIs404() throws Exception {
        when(catalog.find("Q2")).thenReturn(Optional.empty());
        mvc.perform(get("/api/products/Q2")).andExpect(status().isNotFound());
    }
}
