// VERDICT | T12 Exception filters & overflow | BETTER: C#
// WHY: `catch ... when` and `checked { }` blocks; Java catches, inspects and rethrows, and needs Math.*Exact per operation. (Java wins multi-catch.)

package shop.t12_errors;

/**
 * Integer overflow wraps silently in Java. Detecting it means calling Math.*Exact
 * for every single operation - there is no checked block or project-wide switch.
 */
public final class StockMath {

    private StockMath() {}

    public static int stockValueCentsUnchecked(int quantity, int unitPriceCents) {
        return quantity * unitPriceCents;
    }

    public static int stockValueCents(int quantity, int unitPriceCents) {
        return Math.multiplyExact(quantity, unitPriceCents);
    }

    public static int totalValueCents(int[] quantities, int[] unitPriceCents) {
        int total = 0;
        for (int i = 0; i < quantities.length; i++) {
            total = Math.addExact(total, Math.multiplyExact(quantities[i], unitPriceCents[i]));
        }
        return total;
    }
}
