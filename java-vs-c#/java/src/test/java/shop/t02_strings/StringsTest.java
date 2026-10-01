// VERDICT | T02 Strings | BETTER: C#
// WHY: interpolation puts each value where it is printed ({x,-12} {y:F2}); Java only has positional %s placeholders.

package shop.t02_strings;

import org.junit.jupiter.api.Test;
import shop.domain.SampleData;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringsTest {

    @Test
    void invoiceLineIsPaddedAndAligned() {
        var line = SampleData.orders().getFirst().lines().getFirst();
        assertEquals("Clean Code   x 2    71.00", InvoiceFormatter.line(line));
    }

    @Test
    void greetingPluralises() {
        assertEquals("Hello Ana, you have 1 order.", InvoiceFormatter.greeting("Ana", 1));
        assertEquals("Hello Ana, you have 2 orders.", InvoiceFormatter.greeting("Ana", 2));
    }

    @Test
    void invoiceHasHeaderLinesAndTotal() {
        var lines = InvoiceFormatter.invoice(SampleData.orders().getFirst()).lines().toList();
        assertEquals(6, lines.size());
        assertEquals("INVOICE #1", lines.get(0));
        assertEquals("Date:     2026-01-10", lines.get(2));
        assertEquals("TOTAL:    160.99", lines.get(5));
    }

    @Test
    void jsonContainsQuotedValues() {
        assertEquals("{\"id\": 1, \"customer\": \"Ana\", \"total\": 160.99}",
                InvoiceFormatter.toJson(SampleData.orders().getFirst()));
    }
}
