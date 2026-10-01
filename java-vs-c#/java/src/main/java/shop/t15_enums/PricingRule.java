// VERDICT | T15 Enums | BETTER: JAVA
// WHY: enum constants carry fields, own method bodies and implement interfaces; C# enums are plain ints ((Enum)42 is legal).

package shop.t15_enums;

import java.math.BigDecimal;

public interface PricingRule {

    BigDecimal cost(BigDecimal subtotal);
}
