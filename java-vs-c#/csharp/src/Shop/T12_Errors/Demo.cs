// VERDICT | T12 Exception filters & overflow | BETTER: C#
// WHY: `catch ... when` and `checked { }` blocks; Java catches, inspects and rethrows, and needs Math.*Exact per operation. (Java wins multi-catch.)

namespace Shop.T12_Errors;

public static class Demo
{
    public static void Run()
    {
        var busy = new PaymentClient(_ => throw new GatewayException(503), []);
        var rejecting = new PaymentClient(_ => throw new GatewayException(402), []);
        Console.WriteLine($"503 -> {busy.Charge(10m)}");
        Console.WriteLine($"402 -> {rejecting.Charge(10m)}");
        var offline = new PaymentClient(_ => throw new ConnectionLostException("socket closed"), []);
        Console.WriteLine($"multi-catch -> {offline.ChargeOrNetworkError(10m)}");

        Console.WriteLine($"Unchecked 50_000 * 99_999 = {StockMath.StockValueCentsUnchecked(50_000, 99_999)}");
        try
        {
            StockMath.StockValueCents(50_000, 99_999);
        }
        catch (OverflowException e)
        {
            Console.WriteLine($"Checked: {e.Message}");
        }
    }
}
