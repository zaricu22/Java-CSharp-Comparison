// VERDICT | T02 Strings | BETTER: C#
// WHY: interpolation puts each value where it is printed ({x,-12} {y:F2}); Java only has positional %s placeholders.

package shop.t02_strings;

import shop.domain.SampleData;

public final class Demo {

    public static void run() {
        var order = SampleData.orders().getFirst();
        System.out.println(InvoiceFormatter.greeting("Ana", 2));
        System.out.println(InvoiceFormatter.invoice(order));
        System.out.println(InvoiceFormatter.toJson(order));
    }
}
