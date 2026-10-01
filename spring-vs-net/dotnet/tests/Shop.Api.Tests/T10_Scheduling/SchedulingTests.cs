// VERDICT | T10 Scheduling | BETTER: SPRING
// WHY: @Scheduled(fixedRate / cron) on any method; ASP.NET needs a BackgroundService with a PeriodicTimer loop and has no built-in cron.

using Microsoft.AspNetCore.Hosting;
using Shop.Api.T10_Scheduling;

namespace Shop.Api.Tests.T10_Scheduling;

public class SchedulingTests
{
    [Fact]
    public async Task JobRunsRepeatedlyAtTheConfiguredInterval()
    {
        var factory = new ShopApiFactory().WithWebHostBuilder(host => host.UseSetting("Shop:Jobs:CleanupInterval", "00:00:00.050"));
        factory.CreateClient(); // starts the host and its hosted services
        var job = factory.Services.GetRequiredService<ReservationCleanupJob>();

        var deadline = DateTime.UtcNow.AddSeconds(5);
        while (job.Runs < 3 && DateTime.UtcNow < deadline)
        {
            await Task.Delay(20);
        }
        Assert.True(job.Runs >= 3, $"runs: {job.Runs}");
    }
}
