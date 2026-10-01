// VERDICT | T12 Exception filters & overflow | BETTER: C#
// WHY: `catch ... when` and `checked { }` blocks; Java catches, inspects and rethrows, and needs Math.*Exact per operation. (Java wins multi-catch.)

namespace Shop.T12_Errors;

public class GatewayException(int statusCode) : Exception($"Gateway returned {statusCode}")
{
    public int StatusCode { get; } = statusCode;
}

/// <summary>
/// Exception filters ("when"): a catch block only runs if the condition holds; otherwise the
/// exception keeps flying untouched - no catch-inspect-rethrow, and the stack is not unwound.
/// </summary>
public sealed class PaymentClient(Func<decimal, string> gateway, List<string> log)
{
    public string Charge(decimal amount)
    {
        try
        {
            return gateway(amount);
        }
        catch (GatewayException e) when (e.StatusCode == 503)
        {
            return "retry-later";
        }
        catch (GatewayException e) when (e.StatusCode is >= 400 and < 500)
        {
            return $"rejected:{e.StatusCode}";
        }
    }

    /// <summary>Log every failure without catching it: the filter returns false.</summary>
    public string ChargeWithAudit(decimal amount)
    {
        try
        {
            return gateway(amount);
        }
        catch (Exception e) when (Audit(e))
        {
            throw; // never reached - Audit always returns false
        }
    }

    private bool Audit(Exception e)
    {
        log.Add($"payment failed: {e.Message}");
        return false;
    }

    /// <summary>
    /// Where Java wins: no multi-catch syntax. The closest C# form catches Exception and
    /// filters by type, so "e" stays typed as Exception (or you duplicate the catch block).
    /// </summary>
    public string ChargeOrNetworkError(decimal amount)
    {
        try
        {
            return gateway(amount);
        }
        catch (Exception e) when (e is ConnectionLostException or GatewayTimeoutException)
        {
            return $"network-error: {e.Message}";
        }
    }
}

public class ConnectionLostException(string message) : Exception(message);

public class GatewayTimeoutException(string message) : Exception(message);

/// <summary>
/// checked/unchecked: one keyword covers a whole expression or block
/// (or the whole project via &lt;CheckForOverflowUnderflow&gt;).
/// </summary>
public static class StockMath
{
    public static int StockValueCentsUnchecked(int quantity, int unitPriceCents) => unchecked(quantity * unitPriceCents);

    public static int StockValueCents(int quantity, int unitPriceCents) => checked(quantity * unitPriceCents);

    public static int TotalValueCents(int[] quantities, int[] unitPriceCents)
    {
        checked
        {
            var total = 0;
            for (var i = 0; i < quantities.Length; i++)
            {
                total += quantities[i] * unitPriceCents[i];
            }
            return total;
        }
    }
}
