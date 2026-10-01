// VERDICT | T10 Scheduling | BETTER: SPRING
// WHY: @Scheduled(fixedRate / cron) on any method; ASP.NET needs a BackgroundService with a PeriodicTimer loop and has no built-in cron.

package shop.t10_scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/** Enabled by @EnableScheduling on ShopApplication. */
@Component
public class MaintenanceJobs {

    private final AtomicInteger cleanupRuns = new AtomicInteger();
    private final AtomicInteger nightlyReports = new AtomicInteger();

    /** Interval comes from configuration (ISO-8601 duration, e.g. PT1H). */
    @Scheduled(fixedRateString = "${shop.jobs.cleanup-rate}")
    public void releaseExpiredReservations() {
        cleanupRuns.incrementAndGet();
    }

    /** Every day at 03:00 - cron is built in (6 fields: sec min hour day month weekday). */
    @Scheduled(cron = "0 0 3 * * *")
    public void nightlySalesReport() {
        nightlyReports.incrementAndGet();
    }

    public int cleanupRuns() {
        return cleanupRuns.get();
    }
}
