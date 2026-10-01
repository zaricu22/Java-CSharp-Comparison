// VERDICT | T11 Resilience (retry) | BETTER: TIE
// WHY: Spring Framework 7 has @Retryable built in; .NET has Microsoft.Extensions.Resilience (Polly v8) pipelines - both are first-party today.

package shop.t11_resilience;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/** Stand-in for a remote exchange-rate API that can be told to fail the next N calls. */
@Component
public class FlakyRatesApi {

    public static class TransientRateException extends RuntimeException {
        public TransientRateException() {
            super("rates API temporarily unavailable");
        }
    }

    private static final Map<String, BigDecimal> RATES = Map.of("USD", new BigDecimal("1.08"), "RSD", new BigDecimal("117.20"));

    private final AtomicInteger failuresLeft = new AtomicInteger();
    private final AtomicInteger calls = new AtomicInteger();

    public void failNext(int times) {
        failuresLeft.set(times);
        calls.set(0);
    }

    public int calls() {
        return calls.get();
    }

    public BigDecimal rate(String currency) {
        calls.incrementAndGet();
        if (failuresLeft.getAndUpdate(n -> Math.max(0, n - 1)) > 0) {
            throw new TransientRateException();
        }
        var rate = RATES.get(currency);
        if (rate == null) {
            throw new IllegalArgumentException("Unknown currency " + currency);
        }
        return rate;
    }
}
