// VERDICT | T02 Dependency injection | BETTER: SPRING
// WHY: component scanning, Map<name, bean> injection and scoped proxies (request bean inside a singleton); ASP.NET registers everything by hand (easier to trace).

package shop.t02_di;

import java.math.BigDecimal;

public interface PaymentProvider {

    BigDecimal fee(BigDecimal amount);
}
