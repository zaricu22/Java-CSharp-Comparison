// VERDICT | T15 Enums | BETTER: JAVA
// WHY: enum constants carry fields, own method bodies and implement interfaces; C# enums are plain ints ((Enum)42 is legal).

package shop.t15_enums;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Where Java wins: an enum is a full class. Each constant carries its own data (fields),
 * its own behaviour (constant-specific method bodies) and the enum implements an interface.
 * Everything about a shipping method lives in one place, and an invalid value cannot exist.
 */
public enum ShippingMethod implements PricingRule {

    STANDARD("Standard delivery", 5) {
        @Override
        public BigDecimal cost(BigDecimal subtotal) {
            return subtotal.compareTo(FREE_SHIPPING_OVER) >= 0 ? BigDecimal.ZERO : new BigDecimal("4.99");
        }
    },
    EXPRESS("Express delivery", 1) {
        @Override
        public BigDecimal cost(BigDecimal subtotal) {
            return new BigDecimal("9.99");
        }
    },
    PICKUP("Store pickup", 0) {
        @Override
        public BigDecimal cost(BigDecimal subtotal) {
            return BigDecimal.ZERO;
        }
    };

    private static final BigDecimal FREE_SHIPPING_OVER = new BigDecimal("50");

    private final String label;
    private final int days;

    ShippingMethod(String label, int days) {
        this.label = label;
        this.days = days;
    }

    public String label() {
        return label;
    }

    public LocalDate eta(LocalDate orderDate) {
        return orderDate.plusDays(days);
    }
}
