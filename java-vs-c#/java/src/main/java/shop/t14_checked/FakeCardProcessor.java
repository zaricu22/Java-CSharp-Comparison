// VERDICT | T14 Checked exceptions | BETTER: JAVA
// WHY: the compiler forces callers to handle declared failures; in C# a forgotten catch compiles and fails at runtime.

package shop.t14_checked;

import java.math.BigDecimal;

public final class FakeCardProcessor implements CardProcessor {

    private static final BigDecimal LIMIT = new BigDecimal("1000");
    private int sequence;

    @Override
    public Receipt charge(String card, BigDecimal amount) throws PaymentDeclinedException {
        if (card.startsWith("0000")) {
            throw new PaymentDeclinedException("card blocked");
        }
        if (amount.compareTo(LIMIT) > 0) {
            throw new PaymentDeclinedException("limit exceeded");
        }
        return new Receipt("R-" + (++sequence), amount);
    }
}
