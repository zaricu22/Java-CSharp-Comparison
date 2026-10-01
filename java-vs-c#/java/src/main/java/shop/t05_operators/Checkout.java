// VERDICT | T05 Operators & value types | BETTER: C#
// WHY: operator overloading, structs, decimal and unsigned types give `(price * qty + ship) * 0.9m`; Java chains methods and masks bytes.

package shop.t05_operators;

import java.math.BigDecimal;

public final class Checkout {

    private Checkout() {}

    /** Reads right-to-left as a chain: (unitPrice * quantity + shipping) * discountFactor */
    public static Money total(Money unitPrice, int quantity, Money shipping, BigDecimal discountFactor) {
        return unitPrice.times(quantity).plus(shipping).times(discountFactor);
    }

    public static boolean qualifiesForFreeShipping(Money subtotal) {
        return subtotal.compareTo(Money.eur("50")) >= 0;
    }
}
