// VERDICT | T14 Checked exceptions | BETTER: JAVA
// WHY: the compiler forces callers to handle declared failures; in C# a forgotten catch compiles and fails at runtime.

package shop.t14_checked;

import java.math.BigDecimal;
import java.util.List;

public final class CheckoutService {

    private final CardProcessor processor;

    public CheckoutService(CardProcessor processor) {
        this.processor = processor;
    }

    /**
     * Remove the catch block and this file stops compiling:
     * "unreported exception PaymentDeclinedException; must be caught or declared to be thrown".
     * Forgetting to handle a declined payment is impossible.
     */
    public String checkout(String card, BigDecimal amount) {
        try {
            return "PAID " + processor.charge(card, amount).id();
        } catch (PaymentDeclinedException e) {
            return "DECLINED " + e.getMessage();
        }
    }

    /**
     * The honest downside: java.util.function interfaces cannot throw checked exceptions,
     * so every stream step that calls such a method needs its own try/catch.
     */
    public List<String> checkoutAll(String card, List<BigDecimal> amounts) {
        return amounts.stream()
                .map(amount -> {
                    try {
                        return processor.charge(card, amount).id();
                    } catch (PaymentDeclinedException _) {
                        return "DECLINED";
                    }
                })
                .toList();
    }
}
