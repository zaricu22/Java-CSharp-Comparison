// VERDICT | T02 Dependency injection | BETTER: SPRING
// WHY: component scanning, Map<name, bean> injection and scoped proxies (request bean inside a singleton); ASP.NET registers everything by hand (easier to trace).

package shop.t02_di;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Three implementations of one interface. Component scanning finds them - there is no
 * registration code anywhere; the bean name ("card", "paypal", "bank") is the key.
 */
public final class PaymentProviders {

    private PaymentProviders() {}

    private static BigDecimal percentPlusFixed(BigDecimal amount, String percent, String fixed) {
        return amount.multiply(new BigDecimal(percent)).add(new BigDecimal(fixed)).setScale(2, RoundingMode.HALF_EVEN);
    }

    @Component("card")
    static class CardPayment implements PaymentProvider {
        @Override
        public BigDecimal fee(BigDecimal amount) {
            return percentPlusFixed(amount, "0.015", "0.25");
        }
    }

    @Component("paypal")
    static class PayPalPayment implements PaymentProvider {
        @Override
        public BigDecimal fee(BigDecimal amount) {
            return percentPlusFixed(amount, "0.029", "0.35");
        }
    }

    @Component("bank")
    static class BankTransferPayment implements PaymentProvider {
        @Override
        public BigDecimal fee(BigDecimal amount) {
            return BigDecimal.ZERO.setScale(2);
        }
    }
}
