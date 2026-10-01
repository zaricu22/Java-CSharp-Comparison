// VERDICT | T05 Operators & value types | BETTER: C#
// WHY: operator overloading, structs, decimal and unsigned types give `(price * qty + ship) * 0.9m`; Java chains methods and masks bytes.

package shop.t05_operators;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperatorsTest {

    @Test
    void checkoutTotalCombinesOperations() {
        var total = Checkout.total(Money.eur("35.50"), 2, Money.eur("4.99"), new BigDecimal("0.9"));
        assertEquals(Money.eur("68.39"), total);
    }

    @Test
    void comparisonUsesMethods() {
        assertTrue(Money.eur("71").isGreaterThan(Money.eur("70")));
        assertTrue(Checkout.qualifiesForFreeShipping(Money.eur("50")));
        assertFalse(Checkout.qualifiesForFreeShipping(Money.eur("49.99")));
    }

    @Test
    void mixingCurrenciesFails() {
        assertThrows(IllegalArgumentException.class,
                () -> Money.eur("1").plus(new Money(BigDecimal.ONE, "USD")));
    }

    @Test
    void equalityIgnoresScale() {
        assertNotEquals(new BigDecimal("2.5"), new BigDecimal("2.50")); // raw BigDecimal trap
        assertEquals(Money.eur("2.5"), Money.eur("2.50"));               // fixed by normalising in Money
    }

    @Test
    void arrayElementsStartAsNull() {
        Money[] prices = new Money[3];
        assertNull(prices[0]);
    }

    @Test
    void unsignedBytesNeedMasking() {
        assertEquals(256, PacketReader.checksum(new byte[] {(byte) 0xFF, 0x01}));
        assertEquals(65534, PacketReader.readUInt16(new byte[] {(byte) 0xFF, (byte) 0xFE}, 0));
    }
}
