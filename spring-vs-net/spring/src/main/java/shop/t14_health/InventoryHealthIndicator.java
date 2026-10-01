// VERDICT | T14 Health checks | BETTER: SPRING
// WHY: Actuator returns JSON with details plus db/disk checks out of the box; ASP.NET health checks are simple but need a custom JSON response writer.

package shop.t14_health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import shop.t05_repositories.ProductRepository;

/**
 * Shown at /actuator/health as "inventory" (bean name minus the HealthIndicator suffix),
 * next to the automatic "db" and "diskSpace" checks.
 */
@Component
public class InventoryHealthIndicator implements HealthIndicator {

    private final ProductRepository products;

    public InventoryHealthIndicator(ProductRepository products) {
        this.products = products;
    }

    @Override
    public Health health() {
        long total = products.count();
        if (total == 0) {
            return Health.down().withDetail("reason", "catalog is empty").build();
        }
        return Health.up()
                .withDetail("products", total)
                .withDetail("outOfStock", products.countByStockLessThan(1))
                .build();
    }
}
