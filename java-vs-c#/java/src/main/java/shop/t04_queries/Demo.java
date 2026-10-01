// VERDICT | T04 LINQ vs Streams | BETTER: C#
// WHY: GroupBy/Sum/MaxBy, tuples and query syntax; Java goes through Collectors and needs a record per intermediate shape.

package shop.t04_queries;

import shop.domain.SampleData;

import java.math.BigDecimal;

public final class Demo {

    public static void run() {
        var orders = SampleData.orders();
        System.out.println("By category:  " + SalesReport.revenueByCategory(orders));
        System.out.println("Top 2:        " + SalesReport.topCustomers(orders, 2));
        System.out.println("Best seller:  " + SalesReport.bestSeller(orders));
        System.out.println("Monthly:      " + SalesReport.monthlySales(orders));
        System.out.println("Big spenders: " + SalesReport.bigSpendersIn(orders, "Belgrade", new BigDecimal("200")));
    }
}
