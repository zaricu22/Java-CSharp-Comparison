// VERDICT | T09 Pattern matching | BETTER: TIE
// WHY: C# has relational, tuple and list patterns; Java checks switch exhaustiveness over sealed types at compile time.

package shop.t09_patterns;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * Where Java wins: a sealed hierarchy is a closed set the compiler knows about,
 * so a switch over it needs no default branch and adding a new subtype breaks
 * every non-exhaustive switch at compile time.
 */
public sealed interface PaymentResult {

    record Approved(String transactionId, BigDecimal amount) implements PaymentResult {}

    record Declined(String reason) implements PaymentResult {}

    record Pending(Duration retryAfter) implements PaymentResult {}

    record FraudSuspected(int score) implements PaymentResult {}
}
