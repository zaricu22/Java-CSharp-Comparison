// VERDICT | T15 Enums | BETTER: JAVA
// WHY: enum constants carry fields, own method bodies and implement interfaces; C# enums are plain ints ((Enum)42 is legal).

package shop.t15_enums;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnumsTest {

    @Test
    void eachConstantHasItsOwnCost() {
        var small = new BigDecimal("30.00");
        var big = new BigDecimal("80.00");
        assertEquals(new BigDecimal("4.99"), ShippingMethod.STANDARD.cost(small));
        assertEquals(BigDecimal.ZERO, ShippingMethod.STANDARD.cost(big));
        assertEquals(new BigDecimal("9.99"), ShippingMethod.EXPRESS.cost(big));
        assertEquals(BigDecimal.ZERO, ShippingMethod.PICKUP.cost(small));
    }

    @Test
    void constantsCarryData() {
        assertEquals("Express delivery", ShippingMethod.EXPRESS.label());
        assertEquals(LocalDate.of(2026, 3, 6), ShippingMethod.STANDARD.eta(LocalDate.of(2026, 3, 1)));
    }

    @Test
    void enumIsUsableThroughItsInterface() {
        PricingRule rule = ShippingMethod.STANDARD;
        assertEquals(new BigDecimal("34.99"), ShippingReport.totalWith(rule, new BigDecimal("30.00")));
    }

    @Test
    void enumMapKeepsDeclarationOrder() {
        var quotes = ShippingReport.quotes(new BigDecimal("30.00"));
        assertEquals(List.of(ShippingMethod.STANDARD, ShippingMethod.EXPRESS, ShippingMethod.PICKUP),
                List.copyOf(quotes.keySet()));
        assertTrue(ShippingReport.HOME_DELIVERY.contains(ShippingMethod.EXPRESS));
    }

    @Test
    void invalidValuesCannotExist() {
        assertThrows(IllegalArgumentException.class, () -> ShippingMethod.valueOf("DRONE"));
        assertEquals(3, ShippingMethod.values().length); // there is no way to make a 4th instance
    }
}
