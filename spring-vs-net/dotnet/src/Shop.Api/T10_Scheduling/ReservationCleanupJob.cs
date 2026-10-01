// VERDICT | T10 Scheduling | BETTER: SPRING
// WHY: @Scheduled(fixedRate / cron) on any method; ASP.NET needs a BackgroundService with a PeriodicTimer loop and has no built-in cron.

using Microsoft.Extensions.Options;
using Shop.Api.T03_Config;

namespace Shop.Api.T10_Scheduling;

/// <summary>
/// A hosted service with its own timer loop. Cron schedules ("every day at 03:00") are not built in:
/// they need a library (Cronos, Quartz.NET, Hangfire) or hand-written next-run calculations.
/// </summary>
public sealed class ReservationCleanupJob(IOptions<ShopOptions> options) : BackgroundService
{
    private int _runs;

    public int Runs => Volatile.Read(ref _runs);

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        using var timer = new PeriodicTimer(options.Value.Jobs.CleanupInterval);
        while (await timer.WaitForNextTickAsync(stoppingToken))
        {
            ReleaseExpiredReservations();
        }
    }

    private void ReleaseExpiredReservations() => Interlocked.Increment(ref _runs);
}

public static class SchedulingModule
{
    /// <summary>Registered twice: as a singleton (so tests/others can read it) and as the hosted service.</summary>
    public static IServiceCollection AddJobs(this IServiceCollection services)
    {
        services.AddSingleton<ReservationCleanupJob>();
        services.AddHostedService(sp => sp.GetRequiredService<ReservationCleanupJob>());
        return services;
    }
}
