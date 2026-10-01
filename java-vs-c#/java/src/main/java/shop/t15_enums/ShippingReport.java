// VERDICT | T15 Enums | BETTER: JAVA
// WHY: enum constants carry fields, own method bodies and implement interfaces; C# enums are plain ints ((Enum)42 is legal).

package shop.t15_enums;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class ShippingReport {

    private ShippingReport() {}

    /** EnumSet/EnumMap: bit-vector / array backed collections specialised for enums. */
    public static final Set<ShippingMethod> HOME_DELIVERY = EnumSet.of(ShippingMethod.STANDARD, ShippingMethod.EXPRESS);

    public static Map<ShippingMethod, BigDecimal> quotes(BigDecimal subtotal) {
        Map<ShippingMethod, BigDecimal> result = new EnumMap<>(ShippingMethod.class);
        for (ShippingMethod method : ShippingMethod.values()) {
            result.put(method, method.cost(subtotal));
        }
        return result;
    }

    /** Polymorphism through the interface - the caller does not know it is an enum. */
    public static BigDecimal totalWith(PricingRule rule, BigDecimal subtotal) {
        return subtotal.add(rule.cost(subtotal));
    }
}
