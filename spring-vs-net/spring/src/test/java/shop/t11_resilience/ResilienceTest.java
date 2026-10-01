// VERDICT | T11 Resilience (retry) | BETTER: TIE
// WHY: Spring Framework 7 has @Retryable built in; .NET has Microsoft.Extensions.Resilience (Polly v8) pipelines - both are first-party today.

package shop.t11_resilience;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ResilienceTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    FlakyRatesApi api;

    @Test
    void transientFailuresAreRetried() throws Exception {
        api.failNext(2);
        mvc.perform(get("/api/rates/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rate").value(1.08));
        assertThat(api.calls()).isEqualTo(3); // 1 call + 2 retries
    }

    @Test
    void givesUpAfterMaxRetries() throws Exception {
        api.failNext(10);
        mvc.perform(get("/api/rates/USD")).andExpect(status().isServiceUnavailable());
        assertThat(api.calls()).isEqualTo(4); // 1 call + 3 retries
    }
}
