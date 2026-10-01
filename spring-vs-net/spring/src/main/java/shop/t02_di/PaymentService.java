// VERDICT | T02 Dependency injection | BETTER: SPRING
// WHY: component scanning, Map<name, bean> injection and scoped proxies (request bean inside a singleton); ASP.NET registers everything by hand (easier to trace).

package shop.t02_di;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@Service
public class PaymentService {

    private final Map<String, PaymentProvider> providers;

    /** Spring injects every PaymentProvider bean, keyed by bean name. */
    public PaymentService(Map<String, PaymentProvider> providers) {
        this.providers = providers;
    }

    public Set<String> methods() {
        return new TreeSet<>(providers.keySet());
    }

    public Optional<BigDecimal> fee(String method, BigDecimal amount) {
        return Optional.ofNullable(providers.get(method)).map(p -> p.fee(amount));
    }
}
