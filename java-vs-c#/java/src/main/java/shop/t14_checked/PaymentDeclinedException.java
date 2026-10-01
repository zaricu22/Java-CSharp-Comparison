// VERDICT | T14 Checked exceptions | BETTER: JAVA
// WHY: the compiler forces callers to handle declared failures; in C# a forgotten catch compiles and fails at runtime.

package shop.t14_checked;

/** Extends Exception (not RuntimeException) - so it is CHECKED: callers must handle or declare it. */
public class PaymentDeclinedException extends Exception {

    public PaymentDeclinedException(String reason) {
        super(reason);
    }
}
