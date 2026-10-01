// VERDICT | T15 Enums | BETTER: JAVA
// WHY: enum constants carry fields, own method bodies and implement interfaces; C# enums are plain ints ((Enum)42 is legal).

package shop.t15_enums;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class Demo {

    public static void run() {
        var subtotal = new BigDecimal("30.00");
        System.out.println("Quotes for 30.00: " + ShippingReport.quotes(subtotal));
        System.out.println(ShippingMethod.EXPRESS.label() + " arrives " + ShippingMethod.EXPRESS.eta(LocalDate.of(2026, 3, 1)));
        System.out.println("valueOf(\"PICKUP\") = " + ShippingMethod.valueOf("PICKUP").label());
        System.out.println("Total with standard: " + ShippingReport.totalWith(ShippingMethod.STANDARD, subtotal));
    }
}
