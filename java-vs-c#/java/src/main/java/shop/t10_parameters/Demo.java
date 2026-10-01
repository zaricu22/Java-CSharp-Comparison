// VERDICT | T10 Method parameters | BETTER: C#
// WHY: optional/named arguments, out/ref and tuple returns; Java needs parameter objects, result records and try/catch parsing.

package shop.t10_parameters;

import shop.domain.SampleData;

import java.math.BigDecimal;

public final class Demo {

    public static void run() {
        var hundred = new BigDecimal("100");
        System.out.println(Parameters.quote(hundred));
        System.out.println(Parameters.quote(hundred, QuoteOptions.defaults().withExpress(true)));
        System.out.println(Parameters.quote(hundred, QuoteOptions.defaults()
                .withDiscountPercent(BigDecimal.TEN).withCurrency("USD")));

        var range = Parameters.priceRange(SampleData.products());
        System.out.println("Prices from " + range.min() + " to " + range.max());

        Parameters.parseQuantity("3").ifPresentOrElse(
                q -> System.out.println("Parsed quantity " + q),
                () -> System.out.println("Invalid quantity"));
    }
}
