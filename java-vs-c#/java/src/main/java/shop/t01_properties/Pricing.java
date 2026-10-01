// VERDICT | T01 Properties & records | BETTER: C#
// WHY: properties (`field`, `required`, `init`) and `with` replace Java's hand-written getters/setters and manual record copies.

package shop.t01_properties;

import shop.domain.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Pricing {

    private Pricing() {}

    /** Records are immutable, but Java has no "with" expression: every component is copied by hand. */
    public static Product discounted(Product p, int percent) {
        BigDecimal factor = BigDecimal.valueOf(100 - percent).movePointLeft(2);
        return new Product(p.sku(), p.name(), p.category(),
                p.price().multiply(factor).setScale(2, RoundingMode.HALF_EVEN));
    }
}
