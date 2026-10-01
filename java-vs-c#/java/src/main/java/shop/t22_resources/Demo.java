// VERDICT | T22 Resource cleanup | BETTER: TIE
// WHY: C# `using var` needs no nesting; Java try-with-resources keeps the original exception and records close() failures as suppressed.

package shop.t22_resources;

import java.util.ArrayList;
import java.util.Arrays;

public final class Demo {

    public static void run() {
        var journal = new ArrayList<String>();
        Exporter.exportOrders(journal, "1,Ana", false);
        System.out.println("Journal: " + journal);

        try {
            Exporter.exportOrders(new ArrayList<>(), "", true); // body AND close fail
        } catch (IllegalStateException e) {
            System.out.println("Caught: " + e.getMessage() + ", suppressed: "
                    + Arrays.stream(e.getSuppressed()).map(Throwable::getMessage).toList());
        }
    }
}
