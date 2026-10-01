// VERDICT | T03 Configuration & profiles | BETTER: SPRING
// WHY: @Profile and @ConditionalOnProperty swap beans declaratively; ASP.NET binds options just as well, but conditional registration is hand-written if/else.

package shop.t03_config;

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
class ConfigDefaultsTest {

    @Autowired
    MockMvc mvc;

    @Test
    void defaultProfileUsesLogSenderAndDefaults() throws Exception {
        mvc.perform(get("/api/config"))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.freeShippingOver").value(50))
                .andExpect(jsonPath("$.notificationChannel").value("log"))
                .andExpect(jsonPath("$.recommendations").value(false));
    }

    @Test
    void featureFlagOffMeansNoBean() throws Exception {
        mvc.perform(get("/api/recommendations/B1")).andExpect(status().isNotFound());
    }
}
