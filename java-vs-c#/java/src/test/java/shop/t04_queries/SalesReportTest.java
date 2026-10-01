// VERDICT | T04 LINQ vs Streams | BETTER: C#
// WHY: GroupBy/Sum/MaxBy, tuples and query syntax; Java goes through Collectors and needs a record per intermediate shape.

package shop.t04_queries;

import org.junit.jupiter.api.Test;
import shop.domain.Category;
import shop.domain.Order;
import shop.domain.SampleData;
import shop.t04_queries.SalesReport.CustomerTotal;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SalesReportTest {

    private final List<Order> orders = SampleData.orders();

    @Test
    void revenueByCategory() {
        assertEquals(Map.of(
                Category.BOOKS, new BigDecimal("113.00"),
                Category.ELECTRONICS, new BigDecimal("518.97"),
                Category.GROCERY, new BigDecimal("99.20")), SalesReport.revenueByCategory(orders));
    }

    @Test
    void topCustomers() {
        assertEquals(List.of(
                new CustomerTotal("Marko", new BigDecimal("286.20")),
                new CustomerTotal("Ana", new BigDecimal("222.99"))), SalesReport.topCustomers(orders, 2));
    }

    @Test
    void bestSeller() {
        var best = SalesReport.bestSeller(orders);
        assertEquals("Coffee", best.name());
        assertEquals(8, best.quantity());
    }

    @Test
    void monthlySales() {
        assertEquals(Map.of(
                YearMonth.of(2026, 1), new BigDecimal("447.19"),
                YearMonth.of(2026, 2), new BigDecimal("283.98")), SalesReport.monthlySales(orders));
    }

    @Test
    void bigSpendersInCity() {
        assertEquals(List.of("Ana: 222.99", "Jelena: 221.98"),
                SalesReport.bigSpendersIn(orders, "Belgrade", new BigDecimal("200")));
        assertEquals(List.of("Ana: 222.99"),
                SalesReport.bigSpendersIn(orders, "Belgrade", new BigDecimal("222")));
    }
}
