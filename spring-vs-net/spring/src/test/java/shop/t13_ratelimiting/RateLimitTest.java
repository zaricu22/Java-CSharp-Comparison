// VERDICT | T13 Rate limiting | BETTER: ASP.NET
// WHY: AddRateLimiter + RequireRateLimiting are built in and partitioned per client; Spring has no rate limiter, so it is a hand-written filter (or Bucket4j).

package shop.t13_ratelimiting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RateLimitTest {

    @Autowired
    MockMvc mvc;

    @Test
    void searchFindsByName() throws Exception {
        mvc.perform(get("/api/search").param("q", "co").header("X-Client-Id", "t13-search"))
                .andExpect(jsonPath("$[*].sku", contains("B1", "G1"))); // Clean Code, Coffee
    }

    @Test
    void fourthRequestInTheWindowIsRejected() throws Exception {
        for (int i = 0; i < 3; i++) {
            mvc.perform(get("/api/search").param("q", "a").header("X-Client-Id", "t13-a")).andExpect(status().isOk());
        }
        mvc.perform(get("/api/search").param("q", "a").header("X-Client-Id", "t13-a"))
                .andExpect(status().isTooManyRequests());
        mvc.perform(get("/api/search").param("q", "a").header("X-Client-Id", "t13-b"))
                .andExpect(status().isOk()); // other clients have their own window
    }
}
