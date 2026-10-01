// VERDICT | T12 Exception filters & overflow | BETTER: C#
// WHY: `catch ... when` and `checked { }` blocks; Java catches, inspects and rethrows, and needs Math.*Exact per operation. (Java wins multi-catch.)

package shop.t12_errors;

import java.math.BigDecimal;
import java.util.ArrayList;

public final class Demo {

    public static void run() {
        var busy = new PaymentClient(_ -> { throw new GatewayException(503); }, new ArrayList<>());
        var rejecting = new PaymentClient(_ -> { throw new GatewayException(402); }, new ArrayList<>());
        System.out.println("503 -> " + busy.charge(BigDecimal.TEN));
        System.out.println("402 -> " + rejecting.charge(BigDecimal.TEN));
        var offline = new PaymentClient(_ -> { throw new ConnectionLostException("socket closed"); }, new ArrayList<>());
        System.out.println("multi-catch -> " + offline.chargeOrNetworkError(BigDecimal.TEN));

        System.out.println("Unchecked 50_000 * 99_999 = " + StockMath.stockValueCentsUnchecked(50_000, 99_999));
        try {
            StockMath.stockValueCents(50_000, 99_999);
        } catch (ArithmeticException e) {
            System.out.println("Checked: " + e.getMessage());
        }
    }
}
