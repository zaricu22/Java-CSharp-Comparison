// VERDICT | T16 Anonymous & inner classes | BETTER: JAVA
// WHY: anonymous classes implement multi-method interfaces inline and inner classes see the outer instance; C# needs named classes.

package shop.t16_innerclasses;

import org.junit.jupiter.api.Test;
import shop.domain.SampleData;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InnerClassesTest {

    @Test
    void innerItemReadsOuterCart() {
        var cart = new ShoppingCart();
        var books = cart.add(SampleData.CLEAN_CODE, 2);
        var keyboard = cart.add(SampleData.KEYBOARD, 1);

        assertEquals(new BigDecimal("160.99"), cart.subtotal());
        assertEquals(new BigDecimal("55.9"), keyboard.shareOfCart());
        assertEquals(new BigDecimal("44.1"), books.shareOfCart());
    }

    @Test
    void innerItemChangesOuterCart() {
        var cart = new ShoppingCart();
        var books = cart.add(SampleData.CLEAN_CODE, 2);
        var keyboard = cart.add(SampleData.KEYBOARD, 1);

        books.increase();
        keyboard.remove();

        assertEquals(1, cart.size());
        assertEquals(new BigDecimal("106.50"), cart.subtotal());
    }

    @Test
    void anonymousRuleKeepsItsOwnState() {
        var rule = Discounts.oneTimePercent(new BigDecimal("100"), 10);
        var subtotal = new BigDecimal("160.99");

        assertEquals("10% once over 100", rule.name());
        assertEquals(new BigDecimal("144.89"), rule.apply(subtotal));
        assertEquals(subtotal, rule.apply(subtotal)); // already used
        assertEquals(new BigDecimal("50"), Discounts.oneTimePercent(new BigDecimal("100"), 10).apply(new BigDecimal("50")));
    }
}
