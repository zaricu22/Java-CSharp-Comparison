// VERDICT | T16 Anonymous & inner classes | BETTER: JAVA
// WHY: anonymous classes implement multi-method interfaces inline and inner classes see the outer instance; C# needs named classes.

package shop.t16_innerclasses;

import java.math.BigDecimal;

public interface DiscountRule {

    String name();

    boolean appliesTo(BigDecimal subtotal);

    BigDecimal discount(BigDecimal subtotal);

    default BigDecimal apply(BigDecimal subtotal) {
        return appliesTo(subtotal) ? subtotal.subtract(discount(subtotal)) : subtotal;
    }
}
