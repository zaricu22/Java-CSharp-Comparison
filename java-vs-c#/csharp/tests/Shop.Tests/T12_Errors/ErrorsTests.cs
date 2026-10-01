// VERDICT | T12 Exception filters & overflow | BETTER: C#
// WHY: `catch ... when` and `checked { }` blocks; Java catches, inspects and rethrows, and needs Math.*Exact per operation. (Java wins multi-catch.)

namespace Shop.T12_Errors;

public class ErrorsTests
{
    private static PaymentClient FailingWith(int status, List<string> log) =>
        new(_ => throw new GatewayException(status), log);

    [Fact]
    public void HandledStatusCodes()
    {
        Assert.Equal("retry-later", FailingWith(503, []).Charge(10m));
        Assert.Equal("rejected:402", FailingWith(402, []).Charge(10m));
        Assert.Equal("OK-10", new PaymentClient(amount => $"OK-{amount}", []).Charge(10m));
    }

    [Fact]
    public void UnhandledStatusCodePropagates()
    {
        var ex = Assert.Throws<GatewayException>(() => FailingWith(500, []).Charge(10m));
        Assert.Equal(500, ex.StatusCode);
    }

    [Fact]
    public void AuditLogsAndRethrows()
    {
        List<string> log = [];
        Assert.Throws<GatewayException>(() => FailingWith(500, log).ChargeWithAudit(10m));
        Assert.Equal(["payment failed: Gateway returned 500"], log);
    }

    [Fact]
    public void MultiCatchNeedsTypeFilter()
    {
        var offline = new PaymentClient(_ => throw new ConnectionLostException("socket closed"), []);
        var slow = new PaymentClient(_ => throw new GatewayTimeoutException("no answer in 30s"), []);
        Assert.Equal("network-error: socket closed", offline.ChargeOrNetworkError(10m));
        Assert.Equal("network-error: no answer in 30s", slow.ChargeOrNetworkError(10m));
        Assert.Throws<GatewayException>(() => FailingWith(500, []).ChargeOrNetworkError(10m));
    }

    [Fact]
    public void OverflowWrapsSilentlyByDefault()
    {
        Assert.Equal(704_982_704, StockMath.StockValueCentsUnchecked(50_000, 99_999)); // should be 4_999_950_000
    }

    [Fact]
    public void OverflowIsDetectedWhenChecked()
    {
        Assert.Equal(1_999_800, StockMath.StockValueCents(20, 99_990));
        Assert.Throws<OverflowException>(() => StockMath.StockValueCents(50_000, 99_999));
        Assert.Throws<OverflowException>(() => StockMath.TotalValueCents([20_000, 20_000], [99_999, 99_999]));
    }
}
