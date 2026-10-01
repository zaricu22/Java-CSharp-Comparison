// VERDICT | T14 Health checks | BETTER: SPRING
// WHY: Actuator returns JSON with details plus db/disk checks out of the box; ASP.NET health checks are simple but need a custom JSON response writer.

package shop.t14_health;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HealthTest {

    @Autowired
    MockMvc mvc;

    @Test
    void healthIncludesCustomAndBuiltInChecks() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components.inventory.status").value("UP"))
                .andExpect(jsonPath("$.components.inventory.details.products").value(5))
                .andExpect(jsonPath("$.components.inventory.details.outOfStock").value(1))
                .andExpect(jsonPath("$.components.db.status").value("UP")); // automatic, no code
    }
}
