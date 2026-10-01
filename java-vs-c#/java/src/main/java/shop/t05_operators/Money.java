// VERDICT | T05 Operators & value types | BETTER: C#
// WHY: operator overloading, structs, decimal and unsigned types give `(price * qty + ship) * 0.9m`; Java chains methods and masks bytes.

package shop.t05_operators;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * No operator overloading and no user-defined value types: Money is a heap object,
 * arithmetic is method chaining, comparison is compareTo() and a Money[] starts full of nulls.
 */
public record Money(BigDecimal amount, String currency) implements Comparable<Money> {

    public Money {
        Objects.requireNonNull(amount);
        Objects.requireNonNull(currency);
        // BigDecimal.equals() is scale-sensitive (2.5 != 2.50), so records must normalise the scale
        amount = amount.setScale(2, RoundingMode.HALF_EVEN);
    }

    public static Money eur(String amount) {
        return new Money(new BigDecimal(amount), "EUR");
    }

    public Money plus(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money minus(Money other) {
        requireSameCurrency(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    public Money times(int quantity) {
        return new Money(amount.multiply(BigDecimal.valueOf(quantity)), currency);
    }

    public Money times(BigDecimal factor) {
        return new Money(amount.multiply(factor), currency);
    }

    public boolean isGreaterThan(Money other) {
        return compareTo(other) > 0;
    }

    @Override
    public int compareTo(Money other) {
        requireSameCurrency(other);
        return amount.compareTo(other.amount);
    }

    private void requireSameCurrency(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch: " + currency + " vs " + other.currency);
        }
    }

    @Override
    public String toString() {
        return "%.2f %s".formatted(amount, currency);
    }
}
