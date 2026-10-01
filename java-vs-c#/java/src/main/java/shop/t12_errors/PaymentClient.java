// VERDICT | T12 Exception filters & overflow | BETTER: C#
// WHY: `catch ... when` and `checked { }` blocks; Java catches, inspects and rethrows, and needs Math.*Exact per operation. (Java wins multi-catch.)

package shop.t12_errors;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

/**
 * No exception filters: Java must catch first, inspect, and rethrow what it did not want.
 * By then the stack is already unwound, and every catch block grows an if-chain.
 */
public final class PaymentClient {

    private final Function<BigDecimal, String> gateway; // standard function type, like C#'s Func<decimal, string>
    private final List<String> log;

    public PaymentClient(Function<BigDecimal, String> gateway, List<String> log) {
        this.gateway = gateway;
        this.log = log;
    }

    public String charge(BigDecimal amount) {
        try {
            return gateway.apply(amount);
        } catch (GatewayException e) {
            if (e.statusCode() == 503) {
                return "retry-later";
            }
            if (e.statusCode() >= 400 && e.statusCode() < 500) {
                return "rejected:" + e.statusCode();
            }
            throw e;
        }
    }

    /** Log every failure but do not handle it: catch-log-rethrow. */
    public String chargeWithAudit(BigDecimal amount) {
        try {
            return gateway.apply(amount);
        } catch (RuntimeException e) {
            log.add("payment failed: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Where Java wins: multi-catch. One handler for unrelated exception types,
     * and "e" is typed as their closest common supertype.
     */
    public String chargeOrNetworkError(BigDecimal amount) {
        try {
            return gateway.apply(amount);
        } catch (ConnectionLostException | GatewayTimeoutException e) {
            return "network-error: " + e.getMessage();
        }
    }
}
