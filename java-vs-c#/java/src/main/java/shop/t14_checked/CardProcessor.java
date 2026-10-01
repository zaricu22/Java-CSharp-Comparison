// VERDICT | T14 Checked exceptions | BETTER: JAVA
// WHY: the compiler forces callers to handle declared failures; in C# a forgotten catch compiles and fails at runtime.

package shop.t14_checked;

import java.math.BigDecimal;

public interface CardProcessor {

    record Receipt(String id, BigDecimal amount) {}

    /** The failure mode is part of the signature and enforced by the compiler. */
    Receipt charge(String card, BigDecimal amount) throws PaymentDeclinedException;
}
