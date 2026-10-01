// VERDICT | T10 Method parameters | BETTER: C#
// WHY: optional/named arguments, out/ref and tuple returns; Java needs parameter objects, result records and try/catch parsing.

package shop.t10_parameters;

import java.math.BigDecimal;

/**
 * No optional or named arguments: the idiomatic workaround is a parameter object
 * with defaults and "wither" methods (or a Builder, or N overloads).
 */
public record QuoteOptions(BigDecimal discountPercent, boolean express, String currency) {

    public static QuoteOptions defaults() {
        return new QuoteOptions(BigDecimal.ZERO, false, "EUR");
    }

    public QuoteOptions withDiscountPercent(BigDecimal discountPercent) {
        return new QuoteOptions(discountPercent, express, currency);
    }

    public QuoteOptions withExpress(boolean express) {
        return new QuoteOptions(discountPercent, express, currency);
    }

    public QuoteOptions withCurrency(String currency) {
        return new QuoteOptions(discountPercent, express, currency);
    }
}
