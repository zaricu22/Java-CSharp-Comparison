// VERDICT | T02 Strings | BETTER: C#
// WHY: interpolation puts each value where it is printed ({x,-12} {y:F2}); Java only has positional %s placeholders.

package shop.t02_strings;

import shop.domain.Order;
import shop.domain.OrderLine;

import java.util.stream.Collectors;

/**
 * Java has no string interpolation (String Templates were previewed in 21/22 and then withdrawn).
 * The best tools are format specifiers + {@code formatted()} and text blocks:
 * values live far away from the place where they are printed, matched only by position.
 */
public final class InvoiceFormatter {

    private InvoiceFormatter() {}

    public static String line(OrderLine line) {
        return "%-12s x%2d %8.2f".formatted(line.product().name(), line.quantity(), line.total());
    }

    public static String greeting(String name, int orderCount) {
        return "Hello " + name + ", you have " + orderCount + " order" + (orderCount == 1 ? "" : "s") + ".";
    }

    public static String invoice(Order order) {
        String lines = order.lines().stream()
                .map(InvoiceFormatter::line)
                .collect(Collectors.joining("\n"));

        return """
                INVOICE #%d
                Customer: %s (%s)
                Date:     %s
                %s
                TOTAL:    %.2f""".formatted(
                order.id(),
                order.customer().name(), order.customer().city(),
                order.date(),
                lines,
                order.total());
    }

    /** JSON with quotes inside is where text blocks shine - but values are still positional. */
    public static String toJson(Order order) {
        return """
                {"id": %d, "customer": "%s", "total": %.2f}""".formatted(
                order.id(), order.customer().name(), order.total());
    }
}
