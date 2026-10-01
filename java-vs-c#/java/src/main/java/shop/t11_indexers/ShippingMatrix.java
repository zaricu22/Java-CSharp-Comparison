// VERDICT | T11 Indexers & ranges | BETTER: C#
// WHY: catalog["E1"], matrix[1, 1], items[1..^1]; Java spells everything as get()/subList()/substring().

package shop.t11_indexers;

import java.math.BigDecimal;

/** A 2-D lookup: {@code matrix[zone, weightClass]} in C#, {@code matrix.get(zone, weightClass)} here. */
public final class ShippingMatrix {

    private final BigDecimal[][] prices = {
            {new BigDecimal("2.50"), new BigDecimal("5.00"), new BigDecimal("12.00")},  // zone 0: domestic
            {new BigDecimal("9.90"), new BigDecimal("14.90"), new BigDecimal("19.90")}, // zone 1: EU
            {new BigDecimal("19.90"), new BigDecimal("29.90"), new BigDecimal("49.90")} // zone 2: world
    };

    public BigDecimal get(int zone, int weightClass) {
        return prices[zone][weightClass];
    }

    public void set(int zone, int weightClass, BigDecimal price) {
        prices[zone][weightClass] = price;
    }
}
