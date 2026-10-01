// VERDICT | T02 Dependency injection | BETTER: SPRING
// WHY: component scanning, Map<name, bean> injection and scoped proxies (request bean inside a singleton); ASP.NET registers everything by hand (easier to trace).

package shop.t02_di;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@RestController
public class DiController {

    private final PaymentService payments;
    private final RequestInfo requestInfo;
    private final RequestLogger requestLogger;

    public DiController(PaymentService payments, RequestInfo requestInfo, RequestLogger requestLogger) {
        this.payments = payments;
        this.requestInfo = requestInfo;
        this.requestLogger = requestLogger;
    }

    @GetMapping("/api/payments")
    public Set<String> methods() {
        return payments.methods();
    }

    @GetMapping("/api/payments/{method}/fee")
    public Map<String, Object> fee(@PathVariable String method, @RequestParam BigDecimal amount) {
        var fee = payments.fee(method, amount)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown payment method " + method));
        return Map.of("method", method, "fee", fee);
    }

    /** Both ids come from the same request-scoped instance, even though RequestLogger is a singleton. */
    @GetMapping("/api/di/request-ids")
    public Map<String, String> requestIds() {
        return Map.of("controller", requestInfo.id(), "singletonLogger", requestLogger.currentRequestId());
    }
}
