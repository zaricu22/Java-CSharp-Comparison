// VERDICT | T14 Checked exceptions | BETTER: JAVA
// WHY: the compiler forces callers to handle declared failures; in C# a forgotten catch compiles and fails at runtime.

package shop.t14_checked;

import java.math.BigDecimal;
import java.util.List;

public final class Demo {

    public static void run() {
        var service = new CheckoutService(new FakeCardProcessor());
        System.out.println(service.checkout("4111-1111", new BigDecimal("99.00")));
        System.out.println(service.checkout("0000-1111", new BigDecimal("99.00")));
        System.out.println(service.checkoutAll("4111-1111", List.of(new BigDecimal("10"), new BigDecimal("5000"))));
    }
}
