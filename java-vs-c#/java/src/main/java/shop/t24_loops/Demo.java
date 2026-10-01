// VERDICT | T24 Labeled break & continue | BETTER: JAVA
// WHY: `break search;` / `continue orders;` leave or skip an outer loop directly; C# needs goto, a flag variable or an extra method.

package shop.t24_loops;

import shop.domain.SampleData;

import java.util.Set;

public final class Demo {

    public static void run() {
        var orders = SampleData.orders();
        var outOfStock = Set.of("E1"); // Keyboard
        System.out.println(LoopControl.firstUnavailable(orders, outOfStock));
        System.out.println("Shippable orders: " + LoopControl.shippableOrders(orders, outOfStock));
    }
}
