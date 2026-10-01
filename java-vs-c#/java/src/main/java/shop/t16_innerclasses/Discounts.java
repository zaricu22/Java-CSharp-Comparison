// VERDICT | T16 Anonymous & inner classes | BETTER: JAVA
// WHY: anonymous classes implement multi-method interfaces inline and inner classes see the outer instance; C# needs named classes.

package shop.t16_innerclasses;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Where Java wins: anonymous classes implement a multi-method interface inline,
 * with private state, capturing the enclosing parameters. C# has no anonymous
 * interface implementations - every rule needs a named class.
 */
public final class Discounts {

    private Discounts() {}

    public static DiscountRule oneTimePercent(BigDecimal threshold, int percent) {
        return new DiscountRule() {
            private boolean used;

            @Override
            public String name() {
                return percent + "% once over " + threshold;
            }

            @Override
            public boolean appliesTo(BigDecimal subtotal) {
                return !used && subtotal.compareTo(threshold) >= 0;
            }

            @Override
            public BigDecimal discount(BigDecimal subtotal) {
                used = true;
                return subtotal.multiply(BigDecimal.valueOf(percent)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
            }
        };
    }
}
