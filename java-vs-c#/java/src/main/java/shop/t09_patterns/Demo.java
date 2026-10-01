// VERDICT | T09 Pattern matching | BETTER: TIE
// WHY: C# has relational, tuple and list patterns; Java checks switch exhaustiveness over sealed types at compile time.

package shop.t09_patterns;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

public final class Demo {

    public static void run() {
        List<PaymentResult> results = List.of(
                new PaymentResult.Approved("TX-1", new BigDecimal("1500")),
                new PaymentResult.Declined("insufficient funds"),
                new PaymentResult.Pending(Duration.ofSeconds(30)),
                new PaymentResult.FraudSuspected(95));
        results.forEach(r -> System.out.println(Patterns.describe(r)));

        System.out.println("Shipping RS 3kg: " + Patterns.shippingCost("RS", 3));
        System.out.println(Patterns.cartSummary(List.of("Clean Code", "Keyboard", "Coffee")));
    }
}
