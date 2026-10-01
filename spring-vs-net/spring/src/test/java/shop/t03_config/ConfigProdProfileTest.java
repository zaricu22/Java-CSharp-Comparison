// VERDICT | T03 Configuration & profiles | BETTER: SPRING
// WHY: @Profile and @ConditionalOnProperty swap beans declaratively; ASP.NET binds options just as well, but conditional registration is hand-written if/else.

package shop.t03_config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("prod")
class ConfigProdProfileTest {

    @Autowired
    MockMvc mvc;

    @Test
    void prodProfileSwapsBeanAndOverridesValue() throws Exception {
        mvc.perform(get("/api/config"))
                .andExpect(jsonPath("$.notificationChannel").value("smtp"))
                .andExpect(jsonPath("$.freeShippingOver").value(100))
                .andExpect(jsonPath("$.currency").value("EUR")); // not overridden -> inherited from application.yml
    }
}
