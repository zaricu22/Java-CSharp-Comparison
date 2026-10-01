// VERDICT | T09 Pattern matching | BETTER: TIE
// WHY: C# has relational, tuple and list patterns; Java checks switch exhaustiveness over sealed types at compile time.

package shop.t09_patterns;

import org.junit.jupiter.api.Test;
import shop.t09_patterns.PaymentResult.Approved;
import shop.t09_patterns.PaymentResult.Declined;
import shop.t09_patterns.PaymentResult.FraudSuspected;
import shop.t09_patterns.PaymentResult.Pending;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatternsTest {

    @Test
    void describePaymentResults() {
        assertEquals("Approved large payment TX-1", Patterns.describe(new Approved("TX-1", new BigDecimal("1500"))));
        assertEquals("Approved TX-2", Patterns.describe(new Approved("TX-2", new BigDecimal("20"))));
        assertEquals("Declined: expired card", Patterns.describe(new Declined("expired card")));
        assertEquals("Retry in 30s", Patterns.describe(new Pending(Duration.ofSeconds(30))));
        assertEquals("Blocked", Patterns.describe(new FraudSuspected(95)));
        assertEquals("Manual review", Patterns.describe(new FraudSuspected(40)));
    }

    @Test
    void shippingCostByCountryAndWeight() {
        assertEquals(new BigDecimal("2.50"), Patterns.shippingCost("RS", 0.5));
        assertEquals(new BigDecimal("5.00"), Patterns.shippingCost("RS", 3));
        assertEquals(new BigDecimal("12.00"), Patterns.shippingCost("RS", 25));
        assertEquals(new BigDecimal("9.90"), Patterns.shippingCost("AT", 5));
        assertEquals(new BigDecimal("19.90"), Patterns.shippingCost("DE", 6));
        assertEquals(new BigDecimal("29.90"), Patterns.shippingCost("US", 1));
    }

    @Test
    void cartSummaryByShape() {
        assertEquals("Cart is empty", Patterns.cartSummary(List.of()));
        assertEquals("1 item: Coffee", Patterns.cartSummary(List.of("Coffee")));
        assertEquals("2 items: Coffee, Monitor", Patterns.cartSummary(List.of("Coffee", "Monitor")));
        assertEquals("4 items: A ... D", Patterns.cartSummary(List.of("A", "B", "C", "D")));
    }
}
