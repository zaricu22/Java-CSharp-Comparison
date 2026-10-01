// Minimal HTTP load generator: loadgen <url> <requests> <concurrency> [warmupRequests]
// Uses one pooled HttpClient with keep-alive connections; reports req/s, latency p50/p99 and non-200 count.
using System.Diagnostics;

var url = args[0];
var total = int.Parse(args[1]);
var concurrency = int.Parse(args[2]);
var warmup = args.Length > 3 ? int.Parse(args[3]) : 0;

var errorKinds = new System.Collections.Concurrent.ConcurrentDictionary<string, int>();
using var client = new HttpClient(new SocketsHttpHandler { MaxConnectionsPerServer = concurrency, PooledConnectionLifetime = TimeSpan.FromMinutes(10) });

async Task<(double Seconds, long[] LatenciesMicros, int Errors)> RunAsync(int count)
{
    var latencies = new long[count];
    var errors = 0;
    var next = -1;
    var watch = Stopwatch.StartNew();
    var workers = Enumerable.Range(0, concurrency).Select(async _ =>
    {
        int i;
        while ((i = Interlocked.Increment(ref next)) < count)
        {
            var start = Stopwatch.GetTimestamp();
            try
            {
                using var response = await client.GetAsync(url);
                await response.Content.ReadAsByteArrayAsync();
                if ((int)response.StatusCode != 200)
                {
                    Interlocked.Increment(ref errors);
                    errorKinds.AddOrUpdate($"HTTP {(int)response.StatusCode}", 1, (_, n) => n + 1);
                }
            }
            catch (Exception e)
            {
                Interlocked.Increment(ref errors);
                errorKinds.AddOrUpdate($"{e.GetType().Name}: {e.InnerException?.Message ?? e.Message}", 1, (_, n) => n + 1);
            }
            latencies[i] = (long)Stopwatch.GetElapsedTime(start).TotalMicroseconds;
        }
    }).ToArray();
    await Task.WhenAll(workers);
    return (watch.Elapsed.TotalSeconds, latencies, errors);
}

if (warmup > 0) await RunAsync(warmup);
var (seconds, lat, errs) = await RunAsync(total);
Array.Sort(lat);
Console.WriteLine($"{total / seconds:F0} req/s | p50 {lat[lat.Length / 2] / 1000.0:F2} ms | p99 {lat[(int)(lat.Length * 0.99)] / 1000.0:F2} ms | errors {errs}");
foreach (var (kind, count) in errorKinds.OrderByDescending(k => k.Value).Take(5))
{
    Console.Error.WriteLine($"  {count} x {kind}");
}
