// VERDICT | T05 Operators & value types | BETTER: C#
// WHY: operator overloading, structs, decimal and unsigned types give `(price * qty + ship) * 0.9m`; Java chains methods and masks bytes.

package shop.t05_operators;

import java.math.BigDecimal;

public final class Demo {

    public static void run() {
        Money book = Money.eur("35.50");
        Money shipping = Money.eur("4.99");
        System.out.println("Total: " + Checkout.total(book, 2, shipping, new BigDecimal("0.9")));
        System.out.println("71.00 > 70? " + book.times(2).isGreaterThan(Money.eur("70")));
        Money[] prices = new Money[3];
        System.out.println("new Money[3], element 0 = " + prices[0]);
        System.out.println("Checksum: " + PacketReader.checksum(new byte[] {(byte) 0xFF, 0x01}));
    }
}
