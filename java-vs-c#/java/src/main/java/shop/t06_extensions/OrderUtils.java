// VERDICT | T06 Extension methods | BETTER: C#
// WHY: extension methods/properties read left-to-right on the object; Java needs static Utils classes called inside-out.

package shop.t06_extensions;

import shop.domain.Order;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public final class OrderUtils {

    private OrderUtils() {}

    public static List<Order> placedIn(List<Order> orders, YearMonth month) {
        return orders.stream().filter(o -> YearMonth.from(o.date()).equals(month)).toList();
    }

    public static List<Order> from(List<Order> orders, String city) {
        return orders.stream().filter(o -> o.customer().city().equals(city)).toList();
    }

    public static BigDecimal revenue(List<Order> orders) {
        return orders.stream().map(Order::total).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
