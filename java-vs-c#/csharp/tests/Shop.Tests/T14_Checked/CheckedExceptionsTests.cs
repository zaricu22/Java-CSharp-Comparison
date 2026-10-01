// VERDICT | T14 Checked exceptions | BETTER: JAVA
// WHY: the compiler forces callers to handle declared failures; in C# a forgotten catch compiles and fails at runtime.

namespace Shop.T14_Checked;

public class CheckedExceptionsTests
{
    [Fact]
    public void SuccessfulCheckout()
    {
        var service = new CheckoutService(new FakeCardProcessor());
        Assert.Equal("PAID R-1", service.Checkout("4111-1111", 99.00m));
    }

    [Fact]
    public void DeclinedPaymentIsHandledWhenYouRemember()
    {
        var service = new CheckoutService(new FakeCardProcessor());
        Assert.Equal("DECLINED card blocked", service.Checkout("0000-1111", 1m));
        Assert.Equal("DECLINED limit exceeded", service.Checkout("4111-1111", 5000m));
    }

    [Fact]
    public void ForgottenHandlingCompilesAndFailsAtRuntime()
    {
        var service = new CheckoutService(new FakeCardProcessor());
        var ex = Assert.Throws<PaymentDeclinedException>(() => service.CheckoutForgettingErrors("0000", 1m));
        Assert.Equal("card blocked", ex.Message);
    }

    [Fact]
    public void LambdasNeedNoSpecialTreatment()
    {
        var service = new CheckoutService(new FakeCardProcessor());
        Assert.Equal(["R-1", "DECLINED", "R-2"], service.CheckoutAll("4111", [10m, 5000m, 1m]));
    }
}
