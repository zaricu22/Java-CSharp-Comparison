// VERDICT | T22 Resource cleanup | BETTER: TIE
// WHY: C# `using var` needs no nesting; Java try-with-resources keeps the original exception and records close() failures as suppressed.

package shop.t22_resources;

import java.util.List;

/**
 * try-with-resources: resources are closed in reverse order, and if both the body and close()
 * throw, the body's exception wins and the close() failure is attached as "suppressed".
 * Nothing is lost. The cost is one more nesting level for the block.
 */
public final class Exporter {

    private Exporter() {}

    public static void exportOrders(List<String> journal, String orderLine, boolean failOnClose) {
        try (var orders = new ExportFile("orders.csv", journal, failOnClose);
             var lines = new ExportFile("lines.csv", journal, false)) {
            orders.write(orderLine);
            lines.write("1,B1,2");
        }
    }
}
