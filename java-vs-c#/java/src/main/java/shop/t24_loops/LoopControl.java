// VERDICT | T24 Labeled break & continue | BETTER: JAVA
// WHY: `break search;` / `continue orders;` leave or skip an outer loop directly; C# needs goto, a flag variable or an extra method.

package shop.t24_loops;

import shop.domain.Order;
import shop.domain.OrderLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Where Java wins: labeled break / continue. A nested loop can stop or skip the OUTER loop
 * directly, and code after the loops still runs in the same method.
 * (Both languages can often avoid nested loops with anyMatch / Any - these methods show
 * the loop form, which is still common in parsing, matrix and search code.)
 */
public final class LoopControl {

    private LoopControl() {}

    /** Stop everything at the first order line whose product is out of stock. */
    public static String firstUnavailable(List<Order> orders, Set<String> outOfStock) {
        String found = null;
        search:
        for (Order order : orders) {
            for (OrderLine line : order.lines()) {
                if (outOfStock.contains(line.product().sku())) {
                    found = "order " + order.id() + ": " + line.product().name();
                    break search;
                }
            }
        }
        return found == null ? "all available" : "Blocked " + found;
    }

    /** Skip a whole order as soon as one of its lines is out of stock. */
    public static List<Integer> shippableOrders(List<Order> orders, Set<String> outOfStock) {
        List<Integer> ids = new ArrayList<>();
        orders:
        for (Order order : orders) {
            for (OrderLine line : order.lines()) {
                if (outOfStock.contains(line.product().sku())) {
                    continue orders;
                }
            }
            ids.add(order.id());
        }
        return ids;
    }
}
