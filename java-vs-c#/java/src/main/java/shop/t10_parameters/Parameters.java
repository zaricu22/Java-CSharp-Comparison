// VERDICT | T10 Method parameters | BETTER: C#
// WHY: optional/named arguments, out/ref and tuple returns; Java needs parameter objects, result records and try/catch parsing.

package shop.t10_parameters;

import shop.domain.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;

public final class Parameters {

    private static final BigDecimal EXPRESS_FEE = new BigDecimal("9.99");
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private Parameters() {}

    // ---- optional / named arguments -> overload + parameter object ----

    public static String quote(BigDecimal subtotal) {
        return quote(subtotal, QuoteOptions.defaults());
    }

    public static String quote(BigDecimal subtotal, QuoteOptions options) {
        BigDecimal total = subtotal
                .multiply(HUNDRED.subtract(options.discountPercent()))
                .divide(HUNDRED, 2, RoundingMode.HALF_EVEN);
        if (options.express()) {
            total = total.add(EXPRESS_FEE);
        }
        return "%.2f %s".formatted(total, options.currency());
    }

    // ---- multiple return values -> a dedicated record type ----

    public record PriceRange(BigDecimal min, BigDecimal max) {}

    public static PriceRange priceRange(List<Product> products) {
        var prices = products.stream().map(Product::price).toList();
        return new PriceRange(
                prices.stream().min(Comparator.naturalOrder()).orElseThrow(),
                prices.stream().max(Comparator.naturalOrder()).orElseThrow());
    }

    // ---- "try parse" -> no out parameters, no Integer.tryParse: catch the exception ----

    public static OptionalInt parseQuantity(String input) {
        try {
            int quantity = Integer.parseInt(input.trim());
            return quantity > 0 ? OptionalInt.of(quantity) : OptionalInt.empty();
        } catch (NumberFormatException _) {
            return OptionalInt.empty();
        }
    }

    // ---- ref parameters -> impossible; return a new value instead ----

    public static PriceRange normalize(PriceRange range) {
        return range.min().compareTo(range.max()) <= 0 ? range : new PriceRange(range.max(), range.min());
    }
}
