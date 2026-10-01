// VERDICT | T12 Exception filters & overflow | BETTER: C#
// WHY: `catch ... when` and `checked { }` blocks; Java catches, inspects and rethrows, and needs Math.*Exact per operation. (Java wins multi-catch.)

package shop.t12_errors;

public class ConnectionLostException extends RuntimeException {

    public ConnectionLostException(String message) {
        super(message);
    }
}
