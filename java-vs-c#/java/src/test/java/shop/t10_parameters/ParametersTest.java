// VERDICT | T10 Method parameters | BETTER: C#
// WHY: optional/named arguments, out/ref and tuple returns; Java needs parameter objects, result records and try/catch parsing.

package shop.t10_parameters;

import org.junit.jupiter.api.Test;
import shop.domain.SampleData;
import shop.t10_parameters.Parameters.PriceRange;

import java.math.BigDecimal;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParametersTest {

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    @Test
    void quoteWithDefaults() {
        assertEquals("100.00 EUR", Parameters.quote(HUNDRED));
    }

    @Test
    void quoteWithSomeOptions() {
        assertEquals("109.99 EUR", Parameters.quote(HUNDRED, QuoteOptions.defaults().withExpress(true)));
        assertEquals("90.00 USD", Parameters.quote(HUNDRED,
                QuoteOptions.defaults().withDiscountPercent(BigDecimal.TEN).withCurrency("USD")));
    }

    @Test
    void multipleReturnValues() {
        var range = Parameters.priceRange(SampleData.products());
        assertEquals(new BigDecimal("12.40"), range.min());
        assertEquals(new BigDecimal("249.00"), range.max());
    }

    @Test
    void tryParseQuantity() {
        assertEquals(OptionalInt.of(3), Parameters.parseQuantity(" 3 "));
        assertEquals(OptionalInt.empty(), Parameters.parseQuantity("three"));
        assertEquals(OptionalInt.empty(), Parameters.parseQuantity("0"));
    }

    @Test
    void normalizeSwapsReversedRange() {
        var reversed = new PriceRange(BigDecimal.TEN, BigDecimal.ONE);
        assertEquals(new PriceRange(BigDecimal.ONE, BigDecimal.TEN), Parameters.normalize(reversed));
    }
}
