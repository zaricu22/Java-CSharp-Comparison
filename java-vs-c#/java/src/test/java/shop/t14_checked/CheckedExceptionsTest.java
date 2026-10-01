// VERDICT | T14 Checked exceptions | BETTER: JAVA
// WHY: the compiler forces callers to handle declared failures; in C# a forgotten catch compiles and fails at runtime.

package shop.t14_checked;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CheckedExceptionsTest {

    @Test
    void successfulCheckout() {
        var service = new CheckoutService(new FakeCardProcessor());
        assertEquals("PAID R-1", service.checkout("4111-1111", new BigDecimal("99.00")));
    }

    @Test
    void declinedPaymentIsAlwaysHandled() {
        var service = new CheckoutService(new FakeCardProcessor());
        assertEquals("DECLINED card blocked", service.checkout("0000-1111", BigDecimal.ONE));
        assertEquals("DECLINED limit exceeded", service.checkout("4111-1111", new BigDecimal("5000")));
    }

    @Test
    void callingTheProcessorDirectlyForcesHandling() {
        var processor = new FakeCardProcessor();
        // processor.charge(...) outside a try/catch or a "throws" clause would not compile.
        // assertThrows accepts an Executable that "throws Throwable", which is why it is allowed here.
        var ex = assertThrows(PaymentDeclinedException.class, () -> processor.charge("0000", BigDecimal.ONE));
        assertEquals("card blocked", ex.getMessage());
    }

    @Test
    void streamsNeedTryCatchInsideLambda() {
        var service = new CheckoutService(new FakeCardProcessor());
        assertEquals(List.of("R-1", "DECLINED", "R-2"),
                service.checkoutAll("4111", List.of(BigDecimal.TEN, new BigDecimal("5000"), BigDecimal.ONE)));
    }
}
