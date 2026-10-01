// VERDICT | T11 Resilience (retry) | BETTER: TIE
// WHY: Spring Framework 7 has @Retryable built in; .NET has Microsoft.Extensions.Resilience (Polly v8) pipelines - both are first-party today.

package shop.t11_resilience;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import shop.t11_resilience.FlakyRatesApi.TransientRateException;

import java.math.BigDecimal;
import java.util.Map;

@RestController
public class RateController {

    private final ExchangeRateClient rates;

    public RateController(ExchangeRateClient rates) {
        this.rates = rates;
    }

    @GetMapping("/api/rates/{currency}")
    public Map<String, Object> rate(@PathVariable String currency) {
        BigDecimal rate = rates.rate(currency);
        return Map.of("currency", currency, "rate", rate);
    }

    @ExceptionHandler(TransientRateException.class)
    public ProblemDetail unavailable(TransientRateException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }
}
