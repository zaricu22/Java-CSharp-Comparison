// VERDICT | T04 LINQ vs Streams | BETTER: C#
// WHY: GroupBy/Sum/MaxBy, tuples and query syntax; Java goes through Collectors and needs a record per intermediate shape.

package shop.t04_queries;

import shop.domain.Category;
import shop.domain.Order;
import shop.domain.OrderLine;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Streams are powerful, but grouping/aggregation goes through the Collectors API,
 * BigDecimal has no operators, and there are no tuples - every intermediate shape needs a record.
 */
public final class SalesReport {

    private SalesReport() {}

    public record CustomerTotal(String name, BigDecimal total) {}

    public record ProductQuantity(String name, int quantity) {}

    public static Map<Category, BigDecimal> revenueByCategory(List<Order> orders) {
        return orders.stream()
                .flatMap(o -> o.lines().stream())
                .collect(Collectors.groupingBy(
                        l -> l.product().category(),
                        () -> new EnumMap<>(Category.class),
                        Collectors.reducing(BigDecimal.ZERO, OrderLine::total, BigDecimal::add)));
    }

    public static List<CustomerTotal> topCustomers(List<Order> orders, int n) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.customer().name(),
                        Collectors.reducing(BigDecimal.ZERO, Order::total, BigDecimal::add)))
                .entrySet().stream()
                .map(e -> new CustomerTotal(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(CustomerTotal::total).reversed())
                .limit(n)
                .toList();
    }

    public static ProductQuantity bestSeller(List<Order> orders) {
        return orders.stream()
                .flatMap(o -> o.lines().stream())
                .collect(Collectors.groupingBy(l -> l.product().name(), Collectors.summingInt(OrderLine::quantity)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> new ProductQuantity(e.getKey(), e.getValue()))
                .orElseThrow();
    }

    public static Map<YearMonth, BigDecimal> monthlySales(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        o -> YearMonth.from(o.date()),
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Order::total, BigDecimal::add)));
    }

    /** Customers from a city who spent at least {@code min}, biggest first, formatted as "Name: total". */
    public static List<String> bigSpendersIn(List<Order> orders, String city, BigDecimal min) {
        return orders.stream()
                .filter(o -> o.customer().city().equals(city))
                .collect(Collectors.groupingBy(
                        o -> o.customer().name(),
                        Collectors.reducing(BigDecimal.ZERO, Order::total, BigDecimal::add)))
                .entrySet().stream()
                .filter(e -> e.getValue().compareTo(min) >= 0)
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .map(e -> "%s: %.2f".formatted(e.getKey(), e.getValue()))
                .toList();
    }
}
