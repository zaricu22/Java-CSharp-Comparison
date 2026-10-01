// VERDICT | T11 Resilience (retry) | BETTER: TIE
// WHY: Spring Framework 7 has @Retryable built in; .NET has Microsoft.Extensions.Resilience (Polly v8) pipelines - both are first-party today.

package shop.t11_resilience;

import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import shop.t11_resilience.FlakyRatesApi.TransientRateException;

import java.math.BigDecimal;

/**
 * Spring Framework 7 (Boot 4) has retry built into spring-context - no Spring Retry library
 * needed any more. Enabled by @EnableResilientMethods on ShopApplication.
 */
@Service
public class ExchangeRateClient {

    private final FlakyRatesApi api;

    public ExchangeRateClient(FlakyRatesApi api) {
        this.api = api;
    }

    @Retryable(includes = TransientRateException.class, maxRetries = 3, delay = 10)
    public BigDecimal rate(String currency) {
        return api.rate(currency);
    }
}
