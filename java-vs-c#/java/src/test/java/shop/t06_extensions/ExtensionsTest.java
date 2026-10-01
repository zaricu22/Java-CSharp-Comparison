// VERDICT | T06 Extension methods | BETTER: C#
// WHY: extension methods/properties read left-to-right on the object; Java needs static Utils classes called inside-out.

package shop.t06_extensions;

import org.junit.jupiter.api.Test;
import shop.domain.SampleData;

import java.math.BigDecimal;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExtensionsTest {

    @Test
    void truncate() {
        assertEquals("Refactoring...", StringUtils.truncate("Refactoring improves design", 11));
        assertEquals("Short", StringUtils.truncate("Short", 11));
    }

    @Test
    void slug() {
        assertEquals("clean-code-a-handbook-of-agile-craftsmanship",
                StringUtils.toSlug("  Clean Code: A Handbook of Agile Craftsmanship! "));
    }

    @Test
    void chainedOrderFilters() {
        var orders = SampleData.orders();
        assertEquals(new BigDecimal("160.99"),
                OrderUtils.revenue(OrderUtils.from(OrderUtils.placedIn(orders, YearMonth.of(2026, 1)), "Belgrade")));
        assertEquals(new BigDecimal("731.17"), OrderUtils.revenue(orders));
    }
}
