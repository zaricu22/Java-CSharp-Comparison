// VERDICT | T18 Declarative HTTP clients | BETTER: SPRING
// WHY: an annotated interface becomes a working client (@HttpExchange) and MockRestServiceServer tests it; .NET writes typed HttpClient code by hand (Refit is third-party) and ships no mock server.

using System.Text;
using System.Text.Json;
using Shop.Api.T18_HttpClients;

namespace Shop.Api.Tests.T18_HttpClients;

/// <summary>
/// .NET ships no mock HTTP server for HttpClient, so the fake supplier is a hand-written
/// HttpMessageHandler that answers queued responses and records the requests.
/// </summary>
public class SupplierClientTests
{
    private sealed class FakeSupplier : HttpMessageHandler
    {
        private readonly Queue<(HttpMethod Method, string Url, HttpStatusCode Status, string Body)> _expected = new();

        public List<(HttpMethod Method, string Url, string? Body)> Received { get; } = [];

        public FakeSupplier Expect(HttpMethod method, string url, HttpStatusCode status, string body = "")
        {
            _expected.Enqueue((method, url, status, body));
            return this;
        }

        protected override async Task<HttpResponseMessage> SendAsync(HttpRequestMessage request, CancellationToken ct)
        {
            var body = request.Content is null ? null : await request.Content.ReadAsStringAsync(ct);
            Received.Add((request.Method, request.RequestUri!.ToString(), body));
            var (method, url, status, responseBody) = _expected.Dequeue();
            Assert.Equal(method, request.Method);
            Assert.Equal(url, request.RequestUri!.ToString());
            return new HttpResponseMessage(status) { Content = new StringContent(responseBody, Encoding.UTF8, "application/json") };
        }
    }

    private static ReorderService Reorders(FakeSupplier supplier) =>
        new(new SupplierClient(new HttpClient(supplier) { BaseAddress = new Uri("http://supplier.test/") }));

    [Fact]
    public async Task ReorderChecksStockThenPlacesOrder()
    {
        var supplier = new FakeSupplier()
            .Expect(HttpMethod.Get, "http://supplier.test/supplier/stock/B1", HttpStatusCode.OK, """{"sku": "B1", "available": 50}""")
            .Expect(HttpMethod.Post, "http://supplier.test/supplier/orders", HttpStatusCode.OK, """{"reference": "PO-77"}""");

        Assert.Equal(new ReorderResult("B1", 20, "PO-77"), await Reorders(supplier).ReorderAsync("B1", 20));
        var order = JsonDocument.Parse(supplier.Received[1].Body!).RootElement;
        Assert.Equal(20, order.GetProperty("quantity").GetInt32());
    }

    [Fact]
    public async Task NoOrderWhenSupplierHasTooLittle()
    {
        var supplier = new FakeSupplier()
            .Expect(HttpMethod.Get, "http://supplier.test/supplier/stock/B1", HttpStatusCode.OK, """{"sku": "B1", "available": 5}""");

        await Assert.ThrowsAsync<SupplierOutOfStockException>(() => Reorders(supplier).ReorderAsync("B1", 20));
        Assert.Single(supplier.Received); // only the stock call happened
    }

    [Fact]
    public async Task HttpErrorsBecomeExceptions()
    {
        var supplier = new FakeSupplier().Expect(HttpMethod.Get, "http://supplier.test/supplier/stock/X9", HttpStatusCode.NotFound);

        var error = await Assert.ThrowsAsync<HttpRequestException>(() => Reorders(supplier).ReorderAsync("X9", 1));
        Assert.Equal(HttpStatusCode.NotFound, error.StatusCode); // one exception type; check the status yourself
    }
}
