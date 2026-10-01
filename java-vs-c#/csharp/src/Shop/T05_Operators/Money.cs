// VERDICT | T05 Operators & value types | BETTER: C#
// WHY: operator overloading, structs, decimal and unsigned types give `(price * qty + ship) * 0.9m`; Java chains methods and masks bytes.

namespace Shop.T05_Operators;

/// <summary>
/// A value type (struct) with real operators. It lives inline (on the stack or inside arrays),
/// a Money[] needs no per-element allocation and default(Money) is 0, never null.
/// decimal equality is numeric (2.5m == 2.50m), so no scale normalisation is needed.
/// </summary>
public readonly record struct Money(decimal Amount, string Currency = "EUR") : IComparable<Money>
{
    public static Money Eur(decimal amount) => new(amount);

    public static Money operator +(Money a, Money b) => new(a.Amount + b.Amount, RequireSameCurrency(a, b));

    public static Money operator -(Money a, Money b) => new(a.Amount - b.Amount, RequireSameCurrency(a, b));

    public static Money operator *(Money m, int quantity) => m with { Amount = m.Amount * quantity };

    public static Money operator *(Money m, decimal factor) => m with { Amount = Math.Round(m.Amount * factor, 2) };

    public static bool operator >(Money a, Money b) => a.CompareTo(b) > 0;

    public static bool operator <(Money a, Money b) => a.CompareTo(b) < 0;

    public static bool operator >=(Money a, Money b) => a.CompareTo(b) >= 0;

    public static bool operator <=(Money a, Money b) => a.CompareTo(b) <= 0;

    public int CompareTo(Money other)
    {
        RequireSameCurrency(this, other);
        return Amount.CompareTo(other.Amount);
    }

    private static string RequireSameCurrency(Money a, Money b) =>
        a.Currency == b.Currency ? a.Currency : throw new ArgumentException($"Currency mismatch: {a.Currency} vs {b.Currency}");

    public override string ToString() => $"{Amount:F2} {Currency}";
}

public static class Checkout
{
    /// <summary>Reads like the formula it is.</summary>
    public static Money Total(Money unitPrice, int quantity, Money shipping, decimal discountFactor) =>
        (unitPrice * quantity + shipping) * discountFactor;

    public static bool QualifiesForFreeShipping(Money subtotal) => subtotal >= Money.Eur(50);
}

/// <summary>byte is unsigned (0..255) and ushort/uint/ulong exist: no masking tricks.</summary>
public static class PacketReader
{
    public static int Checksum(ReadOnlySpan<byte> payload)
    {
        var sum = 0;
        foreach (var b in payload)
        {
            sum += b;
        }
        return sum;
    }

    public static ushort ReadUInt16(ReadOnlySpan<byte> data, int offset) =>
        (ushort)(data[offset] << 8 | data[offset + 1]);
}
