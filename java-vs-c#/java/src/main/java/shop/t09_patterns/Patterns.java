// VERDICT | T09 Pattern matching | BETTER: TIE
// WHY: C# has relational, tuple and list patterns; Java checks switch exhaustiveness over sealed types at compile time.

package shop.t09_patterns;

import shop.t09_patterns.PaymentResult.Approved;
import shop.t09_patterns.PaymentResult.Declined;
import shop.t09_patterns.PaymentResult.FraudSuspected;
import shop.t09_patterns.PaymentResult.Pending;

import java.math.BigDecimal;
import java.util.List;

public final class Patterns {

    private static final BigDecimal LARGE = new BigDecimal("1000");

    private Patterns() {}

    /** Record deconstruction + guards. Exhaustive without a default branch. */
    public static String describe(PaymentResult result) {
        return switch (result) {
            case Approved(var tx, var amount) when amount.compareTo(LARGE) > 0 -> "Approved large payment " + tx;
            case Approved(var tx, _) -> "Approved " + tx;
            case Declined(var reason) -> "Declined: " + reason;
            case Pending(var retryAfter) -> "Retry in " + retryAfter.toSeconds() + "s";
            case FraudSuspected(var score) when score >= 90 -> "Blocked";
            case FraudSuspected _ -> "Manual review";
        };
    }

    /**
     * No tuple patterns and no relational patterns ({@code <= 1}, {@code > 10}) in Java 25:
     * switch on one value, then fall back to ternaries.
     */
    public static BigDecimal shippingCost(String country, double weightKg) {
        return switch (country) {
            case "RS" -> weightKg <= 1 ? new BigDecimal("2.50")
                    : weightKg <= 10 ? new BigDecimal("5.00")
                    : new BigDecimal("12.00");
            case "DE", "AT" -> weightKg <= 5 ? new BigDecimal("9.90") : new BigDecimal("19.90");
            default -> new BigDecimal("29.90");
        };
    }

    /** No list patterns: check sizes and index manually. */
    public static String cartSummary(List<String> items) {
        if (items.isEmpty()) {
            return "Cart is empty";
        } else if (items.size() == 1) {
            return "1 item: " + items.getFirst();
        } else if (items.size() == 2) {
            return "2 items: " + items.get(0) + ", " + items.get(1);
        } else {
            return items.size() + " items: " + items.getFirst() + " ... " + items.getLast();
        }
    }
}
