// VERDICT | T19 API versioning | BETTER: SPRING
// WHY: Spring 7 routes by version (header/path/query/media type), rejects unsupported versions and sends Deprecation/Sunset headers; ASP.NET needs the third-party Asp.Versioning package or hand-written branching.

package shop.t19_versioning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiVersioningTest {

    @Autowired
    MockMvc mvc;

    @Test
    void noHeaderMeansVersionOneWithDeprecationHeaders() throws Exception {
        mvc.perform(get("/api/catalog/E1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(89.99))
                .andExpect(header().exists("Deprecation"))
                .andExpect(header().exists("Sunset"));
    }

    @Test
    void versionTwoHasTheNewShape() throws Exception {
        mvc.perform(get("/api/catalog/E1").header("X-API-Version", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price.amount").value(89.99))
                .andExpect(jsonPath("$.price.currency").value("EUR"))
                .andExpect(jsonPath("$.inStock").value(true))
                .andExpect(header().doesNotExist("Deprecation"));
    }

    @Test
    void unsupportedVersionIsRejected() throws Exception {
        mvc.perform(get("/api/catalog/E1").header("X-API-Version", "3")).andExpect(status().isBadRequest());
    }

    @Test
    void unversionedEndpointsAreUnaffected() throws Exception {
        mvc.perform(get("/api/products/E1"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Deprecation"));
    }
}
