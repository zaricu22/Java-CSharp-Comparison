// VERDICT | T06 Extension methods | BETTER: C#
// WHY: extension methods/properties read left-to-right on the object; Java needs static Utils classes called inside-out.

package shop.t06_extensions;

import shop.domain.SampleData;

import java.time.YearMonth;

import static shop.t06_extensions.OrderUtils.from;
import static shop.t06_extensions.OrderUtils.placedIn;
import static shop.t06_extensions.OrderUtils.revenue;

public final class Demo {

    public static void run() {
        var orders = SampleData.orders();

        // Calls nest inside-out: you read the last step first.
        var januaryBelgrade = revenue(from(placedIn(orders, YearMonth.of(2026, 1)), "Belgrade"));
        System.out.println("January revenue from Belgrade: " + januaryBelgrade);

        System.out.println(StringUtils.toSlug("Clean Code: A Handbook of Agile Craftsmanship"));
        System.out.println(StringUtils.truncate("Refactoring improves design", 11));
    }
}
