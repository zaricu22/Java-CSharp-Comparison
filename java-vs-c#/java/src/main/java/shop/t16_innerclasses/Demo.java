// VERDICT | T16 Anonymous & inner classes | BETTER: JAVA
// WHY: anonymous classes implement multi-method interfaces inline and inner classes see the outer instance; C# needs named classes.

package shop.t16_innerclasses;

import shop.domain.SampleData;

import java.math.BigDecimal;

public final class Demo {

    public static void run() {
        var cart = new ShoppingCart();
        var books = cart.add(SampleData.CLEAN_CODE, 2);
        var keyboard = cart.add(SampleData.KEYBOARD, 1);
        System.out.println("Subtotal " + cart.subtotal() + ", keyboard share " + keyboard.shareOfCart() + "%, books " + books.shareOfCart() + "%");

        var rule = Discounts.oneTimePercent(new BigDecimal("100"), 10);
        System.out.println(rule.name() + ": " + rule.apply(cart.subtotal()) + " then " + rule.apply(cart.subtotal()));

        keyboard.remove();
        System.out.println("After removing keyboard: " + cart.size() + " item(s), subtotal " + cart.subtotal());
    }
}
