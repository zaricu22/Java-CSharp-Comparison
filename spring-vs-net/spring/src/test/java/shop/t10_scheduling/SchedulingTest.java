// VERDICT | T10 Scheduling | BETTER: SPRING
// WHY: @Scheduled(fixedRate / cron) on any method; ASP.NET needs a BackgroundService with a PeriodicTimer loop and has no built-in cron.

package shop.t10_scheduling;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "shop.jobs.cleanup-rate=PT0.05S")
class SchedulingTest {

    @Autowired
    MaintenanceJobs jobs;

    @Test
    void jobRunsRepeatedlyAtTheConfiguredRate() throws InterruptedException {
        long deadline = System.currentTimeMillis() + 5_000;
        while (jobs.cleanupRuns() < 3 && System.currentTimeMillis() < deadline) {
            Thread.sleep(20);
        }
        assertThat(jobs.cleanupRuns()).isGreaterThanOrEqualTo(3);
    }
}
