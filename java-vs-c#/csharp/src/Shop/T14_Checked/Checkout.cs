// VERDICT | T14 Checked exceptions | BETTER: JAVA
// WHY: the compiler forces callers to handle declared failures; in C# a forgotten catch compiles and fails at runtime.

namespace Shop.T14_Checked;

public class PaymentDeclinedException(string reason) : Exception(reason);

public record Receipt(string Id, decimal Amount);

public interface ICardProcessor
{
    /// <exception cref="PaymentDeclinedException">Card blocked or over the limit.</exception>
    /// <remarks>This is only documentation - the compiler does not check that callers handle it.</remarks>
    Receipt Charge(string card, decimal amount);
}

public sealed class FakeCardProcessor : ICardProcessor
{
    private const decimal Limit = 1000m;
    private int _sequence;

    public Receipt Charge(string card, decimal amount)
    {
        if (card.StartsWith("0000"))
        {
            throw new PaymentDeclinedException("card blocked");
        }
        if (amount > Limit)
        {
            throw new PaymentDeclinedException("limit exceeded");
        }
        return new Receipt($"R-{++_sequence}", amount);
    }
}

public sealed class CheckoutService(ICardProcessor processor)
{
    public string Checkout(string card, decimal amount)
    {
        try
        {
            return $"PAID {processor.Charge(card, amount).Id}";
        }
        catch (PaymentDeclinedException e)
        {
            return $"DECLINED {e.Message}";
        }
    }

    /// <summary>
    /// Where Java wins: this compiles without a single warning, yet a declined card
    /// crashes the caller at runtime. In Java the same method would not compile.
    /// </summary>
    public string CheckoutForgettingErrors(string card, decimal amount) =>
        $"PAID {processor.Charge(card, amount).Id}";

    public List<string> CheckoutAll(string card, IEnumerable<decimal> amounts) =>
        amounts.Select(amount =>
        {
            try
            {
                return processor.Charge(card, amount).Id;
            }
            catch (PaymentDeclinedException)
            {
                return "DECLINED";
            }
        }).ToList();
}
