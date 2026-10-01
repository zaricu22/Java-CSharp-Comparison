// VERDICT | T12 Exception filters & overflow | BETTER: C#
// WHY: `catch ... when` and `checked { }` blocks; Java catches, inspects and rethrows, and needs Math.*Exact per operation. (Java wins multi-catch.)

package shop.t12_errors;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ErrorsTest {

    private static PaymentClient failingWith(int status, List<String> log) {
        return new PaymentClient(_ -> { throw new GatewayException(status); }, log);
    }

    @Test
    void handledStatusCodes() {
        assertEquals("retry-later", failingWith(503, new ArrayList<>()).charge(BigDecimal.TEN));
        assertEquals("rejected:402", failingWith(402, new ArrayList<>()).charge(BigDecimal.TEN));
        assertEquals("OK-10", new PaymentClient(amount -> "OK-" + amount, new ArrayList<>()).charge(BigDecimal.TEN));
    }

    @Test
    void unhandledStatusCodePropagates() {
        var ex = assertThrows(GatewayException.class, () -> failingWith(500, new ArrayList<>()).charge(BigDecimal.TEN));
        assertEquals(500, ex.statusCode());
    }

    @Test
    void auditLogsAndRethrows() {
        List<String> log = new ArrayList<>();
        assertThrows(GatewayException.class, () -> failingWith(500, log).chargeWithAudit(BigDecimal.TEN));
        assertEquals(List.of("payment failed: Gateway returned 500"), log);
    }

    @Test
    void multiCatchHandlesUnrelatedTypesInOneBlock() {
        var offline = new PaymentClient(_ -> { throw new ConnectionLostException("socket closed"); }, new ArrayList<>());
        var slow = new PaymentClient(_ -> { throw new GatewayTimeoutException("no answer in 30s"); }, new ArrayList<>());
        assertEquals("network-error: socket closed", offline.chargeOrNetworkError(BigDecimal.TEN));
        assertEquals("network-error: no answer in 30s", slow.chargeOrNetworkError(BigDecimal.TEN));
        assertThrows(GatewayException.class, () -> failingWith(500, new ArrayList<>()).chargeOrNetworkError(BigDecimal.TEN));
    }

    @Test
    void overflowWrapsSilentlyByDefault() {
        assertEquals(704_982_704, StockMath.stockValueCentsUnchecked(50_000, 99_999)); // should be 4_999_950_000
    }

    @Test
    void overflowIsDetectedWhenChecked() {
        assertEquals(1_999_800, StockMath.stockValueCents(20, 99_990));
        assertThrows(ArithmeticException.class, () -> StockMath.stockValueCents(50_000, 99_999));
        assertThrows(ArithmeticException.class,
                () -> StockMath.totalValueCents(new int[] {20_000, 20_000}, new int[] {99_999, 99_999}));
    }
}
